package com.group2.parking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "invoice")
//UC-17 (Tạo hóa đơn thanh toán).
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "parking_session_id")
    private ParkingSession parkingSession;

    private Double amount; // Số tiền phải thu

    private String status; // Trạng thái: "UNPAID" (Chưa thanh toán) hoặc "PAID" (Đã thanh toán)

    private LocalDateTime issuedAt; // Thời gian lập hóa đơn

    private LocalDateTime paymentTime; // Thời gian khách trả tiền (Lúc mới tạo sẽ để null)
}