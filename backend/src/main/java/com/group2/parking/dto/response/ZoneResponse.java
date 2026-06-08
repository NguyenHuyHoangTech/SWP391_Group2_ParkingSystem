package com.group2.parking.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ZoneResponse {
    private Integer id;
    private String name;
    private Integer capacity;
    private Integer vehicleTypeId;
    private String vehicleTypeName;
    private Integer floorId;
    private String floorName;
    private String buildingName;

    // Thống kê slot thực tế
    private long totalSlots;
    private long emptySlots;
    private long occupiedSlots;
    private long maintenanceSlots;
}
