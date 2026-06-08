package com.group2.parking.dto;

import lombok.*;

// DTO returned by PBMS-35 occupancy flow endpoints for chart and table rendering.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OccupancyFlowPointResponse {

    // Time-frame label displayed on the occupancy flow chart.
    private String label;
    // Number of vehicles entering during the time frame.
    private Integer entryCount;
    // Number of vehicles exiting during the time frame.
    private Integer exitCount;
}
