package com.group2.parking.dto.response;

import java.time.LocalDateTime;

public record BookingResponse(
        Integer id,
        Integer accountId,
        Integer buildingId,
        Integer vehicleTypeId,
        String licensePlate,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status
) {}
