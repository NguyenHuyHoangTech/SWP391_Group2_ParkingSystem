package com.group2.parking.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FloorRequest {
    private String name;
    private Integer floorLevel;
    private Integer capacity;
    private Integer buildingId;
    private Integer vehicleTypeId;
}
