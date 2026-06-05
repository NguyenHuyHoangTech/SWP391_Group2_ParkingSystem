package com.group2.parking.dto.response;

public record FeeEstimateResponse(
        String licensePlate,
        long parkingMinutes,
        double estimatedFee
) {}