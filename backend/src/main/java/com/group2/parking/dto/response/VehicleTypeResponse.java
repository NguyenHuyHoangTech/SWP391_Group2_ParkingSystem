package com.group2.parking.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleTypeResponse {
    private Integer id;
    private String name;
    private String description;
}
