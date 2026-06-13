package com.group2.parking.controller;

import com.group2.parking.dto.response.FeeEstimateResponse;
import com.group2.parking.service.FeeCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController // <--- THIẾU CÁI NÀY NÓ KHÔNG BIẾT ĐÂY LÀ API
@RequestMapping("/api/fees") // <--- THIẾU CÁI NÀY ĐỂ ĐỊNH DẠNG ĐƯỜNG DẪN
public class FeeController {

    @Autowired
    private FeeCalculationService feeCalculationService;

    //API: Tính toán trước số tiền đỗ xe (dự kiến) của một xe đang nằm trong bãi
    @GetMapping("/estimate")
    public FeeEstimateResponse estimateFee(@RequestParam String licensePlate){
// Gọi Service, truyền biển số và lấy thời gian hiện tại (now) làm thời gian check-out
        return feeCalculationService.caculateFee(licensePlate, LocalDateTime.now());
    }

}
