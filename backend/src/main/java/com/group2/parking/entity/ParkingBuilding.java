package com.group2.parking.entity;

import jakarta.persistence.*;
import lombok.*;

// Entity maps the ParkingBuilding table so validation can verify building assignments.
@Entity
@Table(name = "ParkingBuilding")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingBuilding {

    // Primary key used by staff validation and building references.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Building name shown in parking management screens.
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    // Physical address of the parking building.
    @Column(name = "address", length = 500)
    private String address;

    // Operational status such as OPEN or CLOSED.
    @Column(name = "status", length = 50)
    private String status;
}
