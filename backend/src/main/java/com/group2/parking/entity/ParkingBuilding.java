package com.group2.parking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ParkingBuilding")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingBuilding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "address", length = 500)
    private String address;

    //OPEN / CLOSED
    @Column(name = "status", nullable = false, length = 50)
    private String status;
}
