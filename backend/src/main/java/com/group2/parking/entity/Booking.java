package com.group2.parking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "account_id", nullable = false)
    private Integer accountId;

    @Column(name = "building_id", nullable = false)
    private Integer buildingId;

    @Column(name = "vehicle_type_id", nullable = false)
    private Integer vehicleTypeId;

    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "slot_id")
    private Integer slotId;

    @Column(name = "monthly_ticket_id")
    private Integer monthlyTicketId;

    @Column(name = "booking_type", nullable = false)
    private String bookingType;

    @Column(name = "expected_checkin_time")
    private LocalDateTime expectedCheckinTime;

    @Column(name = "hold_until")
    private LocalDateTime holdUntil;

    @Column(name = "checked_in_at")
    private LocalDateTime checkedInAt;
}