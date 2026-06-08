package com.group2.parking.controller;

import com.group2.parking.entity.Account;
import com.group2.parking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<?> createStaff(@RequestBody Account account) {
        try {
            Account createdStaff = accountService.createStaff(account);
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
            @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Status is required."));
            }
            Account updatedStaff = accountService.updateStatus(id, status.trim());
            return ResponseEntity.ok(updatedStaff);
        } catch (RuntimeException ex) {
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
