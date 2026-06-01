package com.group2.parking.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuildingResponse {
    private Integer id;
    private String name;
    private String address;
    private String status;
}
