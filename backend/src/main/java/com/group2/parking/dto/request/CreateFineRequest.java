package com.group2.parking.dto.request;

import lombok.Data;

@Data
public class CreateFineRequest {
    private Integer sessionId;
    private String reason;
    private Double fineAmount;
}
