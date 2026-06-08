package com.group2.parking.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueStatisticResponse {

    private String label;
    private Double revenue;
    private Integer paymentCount;
}
