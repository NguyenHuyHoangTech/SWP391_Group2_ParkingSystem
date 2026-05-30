package com.group2.parking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ParkingSession")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "account_id")
    private Integer accountId;

    @Column(name = "building_id", nullable = false)
    private Integer buildingId;

    @Column(name = "slot_id")
    private Integer slotId;

    @Column(name = "vehicle_type_id", nullable = false)
    private Integer vehicleTypeId;

    @Column(name = "card_id")
    private Integer cardId;

    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @Column(name = "entry_gate", nullable = false)
    private String entryGate;

    @Column(name = "exit_gate")
    private String exitGate;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    // RESERVED, ACTIVE, COMPLETED, CANCELLED
    @Column(nullable = false)
    private String status;
}