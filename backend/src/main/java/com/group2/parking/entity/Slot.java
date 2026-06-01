package com.group2.parking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Slot")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "zone_id", nullable = false)
    private Integer zoneId;

    @Column(nullable = false)
    private String name;

    // EMPTY, OCCUPIED, MAINTENANCE
    @Column(nullable = false)
    private String status;
}