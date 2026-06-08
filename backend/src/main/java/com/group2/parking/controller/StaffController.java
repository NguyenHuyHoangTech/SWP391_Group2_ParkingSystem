package com.group2.parking.controller;

import com.group2.parking.dto.StaffCreateRequest;
import com.group2.parking.dto.StaffResponse;
import com.group2.parking.dto.StaffStatusUpdateRequest;
import com.group2.parking.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    public ResponseEntity<List<StaffResponse>> getAllStaff() {
        List<StaffResponse> staffList = staffService.getAllStaff();
        return ResponseEntity.ok(staffList);
    }

    @PostMapping
    public ResponseEntity<?> createStaff(@RequestBody StaffCreateRequest request) {
        try {
            StaffResponse createdStaff = staffService.createStaff(request);
            return ResponseEntity.ok(createdStaff);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", getFriendlyDataIntegrityMessage(ex)));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStaffStatus(
            @PathVariable Integer id,
            @RequestBody StaffStatusUpdateRequest request
    ) {
        try {
            StaffResponse updatedStaff = staffService.updateStaffStatus(id, request);
            return ResponseEntity.ok(updatedStaff);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    private String getFriendlyDataIntegrityMessage(DataIntegrityViolationException ex) {
        String message = ex.getMostSpecificCause() == null
                ? ""
                : ex.getMostSpecificCause().getMessage().toLowerCase();

        if (message.contains("username")) {
            return "Username already exists.";
        }

        if (message.contains("email")) {
            return "Email already exists.";
        }

        if (message.contains("phone")) {
            return "Phone number already exists.";
        }

        if (message.contains("building")) {
            return "Building does not exist.";
        }

        return "Invalid staff account details.";
    }
}
