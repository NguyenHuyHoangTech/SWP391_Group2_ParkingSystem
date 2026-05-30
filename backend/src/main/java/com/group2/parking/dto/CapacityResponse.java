package com.group2.parking.dto;

public record CapacityResponse(
        Integer buildingId,
        Integer vehicleTypeId,
        long totalSlots,
        long activeSessions,
        long activeBookings,
        long availableSlots
) {}