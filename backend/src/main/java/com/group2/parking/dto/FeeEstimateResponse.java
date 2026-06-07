//package com.group2.parking.dto;
//
//
//public record FeeEstimateResponse(
//        String licensePlate,
//        long parkingMinutes,
//        double estimatedFee
//) {}

package com.group2.parking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Tự động sinh Get/Set để dịch ra JSON mượt mà
@AllArgsConstructor // Tự tạo Constructor có tham số
@NoArgsConstructor // Tự tạo Constructor rỗng
public class FeeEstimateResponse {
    private String licensePlate;
    private long estimatedMinutes;
    private double estimatedFee;
}