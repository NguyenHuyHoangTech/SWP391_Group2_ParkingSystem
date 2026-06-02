package com.group2.parking.controller;

import com.group2.parking.entity.Account;
import com.group2.parking.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Account createStaff(@RequestBody Account account) {
        return accountService.createStaff(account);
    }

    @PutMapping("/{id}/status")
    public Account updateStatus(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        String status = payload.get("status");
        return accountService.updateStatus(id, status);
    }
}
