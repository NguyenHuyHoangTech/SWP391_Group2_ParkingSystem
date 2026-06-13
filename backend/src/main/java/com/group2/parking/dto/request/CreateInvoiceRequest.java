package com.group2.parking.dto.request;

import lombok.Data;

// UC-17 (Tạo hóa đơn thanh toán).
@Data
public class CreateInvoiceRequest {
     private Integer sessionId; // ID của chiếc xe trong bãi
     private Double amount;     // Số tiền cần thu
}
