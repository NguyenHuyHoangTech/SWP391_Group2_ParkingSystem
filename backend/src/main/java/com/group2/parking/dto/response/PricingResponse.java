package com.group2.parking.dto.response;

import java.util.List;

public record PricingResponse(
        Integer policyId,
        String policyName,
        Integer vehicleTypeId,
        String vehicleTypeName,
        String status,
        List<PricingBlockResponse> blocks
) {
}
