package com.group2.parking.dto.request;

import java.time.LocalDateTime;

public record CreateBookingRequest(
        Integer accountId,
        Integer buildingId,
        Integer vehicleTypeId,
        String licensePlate,
        LocalDateTime startTime,
        LocalDateTime endTime
) {}
