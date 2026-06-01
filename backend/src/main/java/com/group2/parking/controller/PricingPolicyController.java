package com.group2.parking.controller;

import com.group2.parking.dto.PricingPolicyDTO;
import com.group2.parking.entity.PricingPolicy;
import com.group2.parking.service.PricingPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Gắn biển hiệu đây là Lễ tân, chuyên giao tiếp bằng cục JSON
@RequestMapping("/api/pricing-policies") // Đặt tên địa chỉ cho cổng này
public class PricingPolicyController {

    // Gọi Bếp trưởng (Service) ra làm việc
    @Autowired
    private PricingPolicyService pricingPolicyService;

    // 1. Mở cổng cho khách XEM DANH SÁCH (Method GET)
    @GetMapping
    public List<PricingPolicy> getAll() {
        return pricingPolicyService.getAllPolicies();
    }

    // 2. Mở cổng cho khách XEM CHI TIẾT 1 ID (Method GET)
    @GetMapping("/{id}")
    public PricingPolicy getById(@PathVariable Integer id) {
        return pricingPolicyService.getPricingPolicies(id);
    }

    // 3. Mở cổng cho khách THÊM MỚI (Method POST)
    @PostMapping
    public PricingPolicy create(@RequestBody PricingPolicyDTO dto) {
        // @RequestBody: Lệnh này giúp biến cục JSON từ Postman thành đối tượng DTO
        return pricingPolicyService.createPolicy(dto);
    }

    // 4. Mở cổng cho khách XÓA (Method DELETE)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        pricingPolicyService.deletePolicies(id);
    }
}