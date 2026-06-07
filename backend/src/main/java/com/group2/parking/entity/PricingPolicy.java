package com.group2.parking.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;


import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PricingPolicy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PricingPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "vehicle_type_id")
    @ToString.Exclude // CHẶN VÒNG LẶP
    private VehicleType vehicleType;

    @Column(length = 50)
    private String status = "ACTIVE";

    @OneToMany(mappedBy = "pricingPolicy", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("pricingPolicy")
    private List<PricingBlock> blocks = new ArrayList<>(); // danh sách block kèm


}
