package com.group2.parking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ParkingBuilding")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingBuilding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String address;

    private String status;
}
