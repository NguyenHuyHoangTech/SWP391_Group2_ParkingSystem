package com.group2.parking.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FloorResponse {
    private Integer id;
    private String name;
    private Integer floorLevel;
    private Integer capacity;          // sức chứa tối đa của tầng
    private Integer buildingId;
    private String buildingName;
    private Integer vehicleTypeId;
    private String vehicleTypeName;

    // Cũ: tổng capacity đã chia cho zone (giờ đổi tên cho rõ nghĩa)
    private Integer usedCapacity;       // = zoneAllocatedCapacity (giữ để không break)
    private Integer remainingCapacity;  // = capacity - usedCapacity

    // Thống kê slot thực tế
    private long totalSlots;
    private long emptySlots;
    private long occupiedSlots;
    private long maintenanceSlots;
}
