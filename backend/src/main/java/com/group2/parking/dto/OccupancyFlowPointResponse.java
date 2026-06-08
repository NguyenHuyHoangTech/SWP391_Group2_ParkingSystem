package com.group2.parking.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OccupancyFlowPointResponse {

    private String label;
    private Integer entryCount;
    private Integer exitCount;
}
