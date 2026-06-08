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
@CrossOrigin(origins = "http://localhost:5173")
public class StaffController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public List<Account> getStaffList() {
        return accountService.getAllStaffAndManagers();
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
            @RequestBody StaffStatusUpdateRequest request) {
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
