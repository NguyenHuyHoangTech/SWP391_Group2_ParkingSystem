package com.group2.parking.service;

import com.group2.parking.dto.OccupancyFlowPointResponse;
import com.group2.parking.dto.RevenueStatisticResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

// Service contains PBMS-34 and PBMS-35 reporting business logic using SQL joins over parking data.
@Service
@RequiredArgsConstructor
public class ReportingService {

    // Supported grouping filters used by both revenue and occupancy flow reports.
    private static final List<String> PERIODS = List.of("daily", "weekly", "monthly");

    private final JdbcTemplate jdbcTemplate;

    /**
     * Aggregates successful payment revenue for chart display.
     * Revenue is joined from Payment to ParkingSession and optionally scoped to a manager's floor.
     */
    public List<RevenueStatisticResponse> getRevenueStatistics(String period, String role, Integer managerId) {
        String normalizedPeriod = normalizePeriod(period);
        String scopeClause = buildManagerScopeClause(role, managerId);
        String grouping = getRevenueGrouping(normalizedPeriod);

        // Native SQL keeps the aggregation close to the database schema used by the reporting use cases.
        String sql = """
                SELECT %s,
                       SUM(p.amount) AS revenue,
                       COUNT(p.id) AS payment_count
                FROM Payment p
                INNER JOIN ParkingSession ps ON p.session_id = ps.id
                LEFT JOIN Slot s ON ps.slot_id = s.id
                LEFT JOIN ParkingZone pz ON s.zone_id = pz.id
                LEFT JOIN Floor f ON pz.floor_id = f.id
                WHERE p.status = 'SUCCESS'
                  AND p.created_at IS NOT NULL
                  %s
                GROUP BY %s
                ORDER BY sort_year, sort_period
                """.formatted(grouping, scopeClause, getRevenueGroupBy(normalizedPeriod));

        return jdbcTemplate.query(sql, (rs, rowNum) -> RevenueStatisticResponse.builder()
                .label(rs.getString("label"))
                .revenue(rs.getDouble("revenue"))
                .paymentCount(rs.getInt("payment_count"))
                .build());
    }

    /**
     * Calculates vehicle entry and exit flow for chart display.
     * check_in_time contributes entry counts, while check_out_time contributes exit counts.
     */
    public List<OccupancyFlowPointResponse> getOccupancyFlowStatistics(String period, String role, Integer managerId) {
        String normalizedPeriod = normalizePeriod(period);
        String scopeClause = buildManagerScopeClause(role, managerId);
        String dateClause = getFlowDateClause(normalizedPeriod);

        // UNION ALL combines entry and exit events so both flows can be grouped into the same time buckets.
        String sql = """
                SELECT flow.bucket_order,
                       CASE flow.bucket_order
                           WHEN 0 THEN '00:00 - 03:59'
                           WHEN 1 THEN '04:00 - 07:59'
                           WHEN 2 THEN '08:00 - 11:59'
                           WHEN 3 THEN '12:00 - 15:59'
                           WHEN 4 THEN '16:00 - 19:59'
                           ELSE '20:00 - 23:59'
                       END AS label,
                       SUM(flow.entry_count) AS entry_count,
                       SUM(flow.exit_count) AS exit_count
                FROM (
                    SELECT DATEPART(HOUR, ps.check_in_time) / 4 AS bucket_order,
                           1 AS entry_count,
                           0 AS exit_count
                    FROM ParkingSession ps
                    LEFT JOIN Slot s ON ps.slot_id = s.id
                    LEFT JOIN ParkingZone pz ON s.zone_id = pz.id
                    LEFT JOIN Floor f ON pz.floor_id = f.id
                    WHERE ps.check_in_time IS NOT NULL
                      %s
                      %s

                    UNION ALL

                    SELECT DATEPART(HOUR, ps.check_out_time) / 4 AS bucket_order,
                           0 AS entry_count,
                           1 AS exit_count
                    FROM ParkingSession ps
                    LEFT JOIN Slot s ON ps.slot_id = s.id
                    LEFT JOIN ParkingZone pz ON s.zone_id = pz.id
                    LEFT JOIN Floor f ON pz.floor_id = f.id
                    WHERE ps.check_out_time IS NOT NULL
                      %s
                      %s
                ) flow
                GROUP BY flow.bucket_order
                ORDER BY flow.bucket_order
                """.formatted(dateClause.replace("event_time", "ps.check_in_time"), scopeClause,
                dateClause.replace("event_time", "ps.check_out_time"), scopeClause);

        return jdbcTemplate.query(sql, (rs, rowNum) -> OccupancyFlowPointResponse.builder()
                .label(rs.getString("label"))
                .entryCount(rs.getInt("entry_count"))
                .exitCount(rs.getInt("exit_count"))
                .build());
    }

    // Validates and normalizes the requested report period before SQL generation.
    private String normalizePeriod(String period) {
        if (period == null || period.isBlank()) {
            return "daily";
        }

        String normalized = period.trim().toLowerCase(Locale.ROOT);
        if (!PERIODS.contains(normalized)) {
            throw new IllegalArgumentException("Period must be daily, weekly, or monthly.");
        }

        return normalized;
    }

    // Builds the manager-only floor scope; admin requests intentionally receive no additional filter.
    private String buildManagerScopeClause(String role, Integer managerId) {
        if (role == null || role.isBlank() || role.trim().equalsIgnoreCase("ADMIN")) {
            return "";
        }

        if (!role.trim().equalsIgnoreCase("MANAGER")) {
            throw new IllegalArgumentException("Role must be ADMIN or MANAGER.");
        }

        if (managerId == null) {
            throw new IllegalArgumentException("Manager ID is required.");
        }

        return "AND f.manager_id = " + managerId;
    }

    // Select expression for the revenue label and stable sort keys for each period.
    private String getRevenueGrouping(String period) {
        return switch (period) {
            case "weekly" -> """
                    CONCAT(DATEPART(YEAR, p.created_at), '-W', RIGHT('0' + CAST(DATEPART(ISO_WEEK, p.created_at) AS VARCHAR(2)), 2)) AS label,
                    DATEPART(YEAR, p.created_at) AS sort_year,
                    DATEPART(ISO_WEEK, p.created_at) AS sort_period
                    """;
            case "monthly" -> """
                    CONCAT(DATEPART(YEAR, p.created_at), '-', RIGHT('0' + CAST(DATEPART(MONTH, p.created_at) AS VARCHAR(2)), 2)) AS label,
                    DATEPART(YEAR, p.created_at) AS sort_year,
                    DATEPART(MONTH, p.created_at) AS sort_period
                    """;
            default -> """
                    CONVERT(VARCHAR(10), CAST(p.created_at AS DATE), 120) AS label,
                    DATEPART(YEAR, p.created_at) AS sort_year,
                    DATEPART(DAYOFYEAR, p.created_at) AS sort_period
                    """;
        };
    }

    // GROUP BY clause must match the selected period fields for SQL Server aggregation.
    private String getRevenueGroupBy(String period) {
        return switch (period) {
            case "weekly" -> "DATEPART(YEAR, p.created_at), DATEPART(ISO_WEEK, p.created_at)";
            case "monthly" -> "DATEPART(YEAR, p.created_at), DATEPART(MONTH, p.created_at)";
            default -> "CAST(p.created_at AS DATE), DATEPART(YEAR, p.created_at), DATEPART(DAYOFYEAR, p.created_at)";
        };
    }

    // Time window used by PBMS-35 to compare today's, last week's, or last month's flow.
    private String getFlowDateClause(String period) {
        return switch (period) {
            case "weekly" -> "AND event_time >= DATEADD(DAY, -7, GETDATE())";
            case "monthly" -> "AND event_time >= DATEADD(MONTH, -1, GETDATE())";
            default -> "AND CAST(event_time AS DATE) = CAST(GETDATE() AS DATE)";
        };
    }
}
