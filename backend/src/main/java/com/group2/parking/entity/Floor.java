package com.group2.parking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Floor")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Floor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "building_id")
    private ParkingBuilding building;

    private String name;

    @Column(name = "floor_level")
    private Integer floorLevel;

    private Integer capacity;

    @ManyToOne
    @JoinColumn(name = "vehicle_type_id")
    private VehicleType vehicleType;
}
