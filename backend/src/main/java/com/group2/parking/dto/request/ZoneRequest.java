package com.group2.parking.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ZoneRequest {
    private String name;
    private Integer floorId;
    private Integer capacity;
}
