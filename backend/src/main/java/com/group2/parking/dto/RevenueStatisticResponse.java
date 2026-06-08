package com.group2.parking.dto;

import lombok.*;

// DTO returned by PBMS-34 revenue report endpoints for chart and table rendering.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueStatisticResponse {

    // Display label for the grouped day, week, or month.
    private String label;
    // Total successful payment amount in the grouped period.
    private Double revenue;
    // Number of successful payment records included in the grouped period.
    private Integer paymentCount;
}
