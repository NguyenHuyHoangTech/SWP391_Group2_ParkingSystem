package com.group2.parking.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoneResponse {
    private Integer id;
    private String name;
    private Integer capacity;
    private Integer vehicleTypeId;
    private String vehicleTypeName;
    private Integer floorId;
    private String floorName;
    private String buildingName;
}
