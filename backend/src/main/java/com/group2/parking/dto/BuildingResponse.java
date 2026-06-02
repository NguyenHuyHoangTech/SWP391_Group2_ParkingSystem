package com.group2.parking.dto;

//DTO trả dữ liệu cho FE
public record BuildingResponse(
        Integer id,
        String name,
        String address,
        String status
) {
}
