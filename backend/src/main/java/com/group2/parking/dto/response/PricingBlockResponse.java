package com.group2.parking.dto.response;

import java.math.BigDecimal;

public record PricingBlockResponse(
        Integer blockOrder,
        Integer durationHours,
        BigDecimal price
) {}
