package com.group2.parking.repository;

import com.group2.parking.entity.ParkingBuilding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingBuildingRepository extends JpaRepository<ParkingBuilding, Integer> {
    List<ParkingBuilding> findByStatus(String status);
}
