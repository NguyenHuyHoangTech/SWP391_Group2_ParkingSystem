package com.group2.parking.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotResponse {
    private Integer id;
    private String name;

    // EMPTY | OCCUPIED | MAINTENANCE
    private String status;

    private Integer zoneId;
    private String zoneName;
    private String floorName;
    private String buildingName;
}
