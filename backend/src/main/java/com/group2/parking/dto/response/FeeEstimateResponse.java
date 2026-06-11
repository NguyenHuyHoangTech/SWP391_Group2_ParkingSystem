package com.group2.parking.dto.response;

import java.math.BigDecimal;

public record FeeEstimateResponse(
        String licensePlate,
        long parkingMinutes,
        BigDecimal estimatedFee
) {}