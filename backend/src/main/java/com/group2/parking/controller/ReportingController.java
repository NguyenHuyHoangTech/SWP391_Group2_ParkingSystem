package com.group2.parking.controller;

import com.group2.parking.dto.OccupancyFlowPointResponse;
import com.group2.parking.dto.RevenueStatisticResponse;
import com.group2.parking.service.ReportingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingService reportingService;

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
