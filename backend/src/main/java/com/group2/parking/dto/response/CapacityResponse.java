package com.group2.parking.dto.response;

import java.time.LocalDateTime;

public record CapacityResponse(
        Integer buildingId,
        String buildingStatus,
        Integer vehicleTypeId,
        long totalSlots,
        long occupiedSlots,
        long activeBookings,
        long availableSlots,
        LocalDateTime asOf
) {}