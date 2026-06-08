package com.group2.parking.controller;

import com.group2.parking.dto.OccupancyFlowPointResponse;
import com.group2.parking.dto.RevenueStatisticResponse;
import com.group2.parking.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Controller exposes reporting APIs for PBMS-34 revenue statistics and PBMS-35 occupancy flow statistics.
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingService reportingService;

    /**
     * GET /api/reports/revenue returns aggregated revenue data grouped by daily, weekly, or monthly period.
     * Admin receives all revenue, while manager requests can be scoped by managerId.
     */
    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenueStatistics(
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam(defaultValue = "ADMIN") String role,
            @RequestParam(required = false) Integer managerId
    ) {
        try {
            List<RevenueStatisticResponse> data = reportingService.getRevenueStatistics(period, role, managerId);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    /**
     * GET /api/reports/occupancy-flow returns vehicle entry and exit counts grouped by time frames of the day.
     * The same role and managerId parameters are used for admin-wide or manager-scoped reporting.
     */
    @GetMapping("/occupancy-flow")
    public ResponseEntity<?> getOccupancyFlowStatistics(
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam(defaultValue = "ADMIN") String role,
            @RequestParam(required = false) Integer managerId
    ) {
        try {
            List<OccupancyFlowPointResponse> data = reportingService.getOccupancyFlowStatistics(period, role, managerId);
            return ResponseEntity.ok(data);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
}
