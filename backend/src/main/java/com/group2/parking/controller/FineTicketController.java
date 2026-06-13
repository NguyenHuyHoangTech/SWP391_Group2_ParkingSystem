package com.group2.parking.controller;

import com.group2.parking.dto.request.CreateFineRequest;
import com.group2.parking.dto.response.ApiResponse;
import com.group2.parking.entity.FineTicket;
import com.group2.parking.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fine-tickets")
public class FineTicketController {

    @Autowired
    private FineService fineService;

    @PostMapping
    public ApiResponse<FineTicket> createFine(@RequestBody CreateFineRequest request) {
        FineTicket result = fineService.createFine(request.getSessionId(), request.getReason());
        // Dùng ApiResponse để đồng bộ với dự án
        return ApiResponse.ok(result);
    }
}