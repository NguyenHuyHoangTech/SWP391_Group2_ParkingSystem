package com.group2.parking.controller;

import com.group2.parking.dto.response.FeeEstimateResponse;
import com.group2.parking.dto.response.PricingResponse;
import com.group2.parking.service.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicPricingController {

    private final PricingService pricingService;

    // UC-405: PULIC XEM BẢNG GIÁ ĐANG ACTIVE
    @GetMapping("/pricing")
    public List<PricingResponse> getPublicPricing() {
        return pricingService.getPublicPricing();
    }

    //UC-406: TRA CỨU PHÍ
    @GetMapping("/fee-estimate")
    public FeeEstimateResponse getFeeEstimate(@RequestParam String licensePlate) {
        return pricingService.estimateFeeByLicensePlate(licensePlate);
    }
}
