package com.group2.parking.dto;

public record FeeEstimateResponse(
        String licensePlate,
        long parkingMinutes,
        double estimatedFee
) {}