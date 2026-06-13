package com.group2.parking.entity;

import jakarta.persistence.*; // Bắt buộc phải import thư viện này
import lombok.*;

@Entity
@Table(name = "fine_ticket")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FineTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Khai báo id tự động tăng
    private Integer id;

    // Các thuộc tính bên dưới của bạn cứ giữ nguyên
    private String reason;
    private Double fineAmount;
    private String status;

    @ManyToOne
    @JoinColumn(name = "parking_session_id")
    private ParkingSession parkingSession;
}