package com.group2.parking.service;

import com.group2.parking.dto.response.PricingBlockResponse;
import com.group2.parking.dto.response.PricingResponse;
import com.group2.parking.entity.PricingBlock;
import com.group2.parking.entity.PricingPolicy;
import com.group2.parking.repository.PricingBlockRepository;
import com.group2.parking.repository.PublicPricingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final PublicPricingRepository publicPricingRepository;
    private final PricingBlockRepository pricingBlockRepository;

    // UC-405: LẤY DANH SÁCH BẢNG GIÁ ĐANG "ACTIVE" CHO PUBLIC XEM
    public List<PricingResponse> getPublicPricing() {
        return publicPricingRepository.findByStatus("ACTIVE")
                .stream()
                .map(this::toPricingResponse)
                .toList();
    }

    // CONVERT PricingPolicy Entity SANG DTO ĐỂ KHÔNG RETURN TRỰC TIẾP Entity ra FE.
    private PricingResponse toPricingResponse(PricingPolicy policy) {
        List<PricingBlockResponse> blocks = pricingBlockRepository
                .findByPricingPolicy_IdOrderByBlockOrderAsc(policy.getId())
                .stream()
                .map(this::toPricingBlockResponse)
                .toList();

        return new PricingResponse(
                policy.getId(),
                policy.getName(),
                policy.getVehicleType() != null ? policy.getVehicleType().getId() : null,
                policy.getVehicleType() != null ? policy.getVehicleType().getName() : null,
                policy.getStatus(),
                blocks
        );
    }

    // CONVERT MỚI BLOCK GIÁ SANG DTO, GIỮ ĐÚNG THỨ TỰ VÀ SỐ TIỀN.
    private PricingBlockResponse toPricingBlockResponse(PricingBlock block) {
        return new PricingBlockResponse(
                block.getBlockOrder(),
                block.getDurationHours(),
                block.getPrice()
        );
    }
}
