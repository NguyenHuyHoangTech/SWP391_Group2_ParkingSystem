package com.group2.parking.dto.response;

import java.time.LocalDateTime;

public record BookingResponse(
        Integer id,
        Integer accountId,
        Integer buildingId,
        Integer vehicleTypeId,
        String licensePlate,
        String bookingType,
        LocalDateTime expectedCheckinTime,
        LocalDateTime holdUntil,
        LocalDateTime checkedInAt,
        LocalDateTime createdAt,
        String status
) {}
