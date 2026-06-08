package com.group2.parking.dto.response;

public record CapacityResponse(
        Integer buildingId,
        Integer vehicleTypeId,
        long totalSlots,
        long activeSessions,
        long activeBookings,
        long availableSlots
) {}