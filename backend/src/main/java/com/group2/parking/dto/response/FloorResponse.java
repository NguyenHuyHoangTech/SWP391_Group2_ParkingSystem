package com.group2.parking.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FloorResponse {
    private Integer id;
    private String name;
    private Integer floorLevel;
    private Integer capacity;
    private Integer buildingId;
    private String buildingName;
    private Integer vehicleTypeId;
    private String vehicleTypeName;
    private Integer usedCapacity;
    private Integer remainingCapacity;
}
