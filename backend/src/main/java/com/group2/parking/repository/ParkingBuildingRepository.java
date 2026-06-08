package com.group2.parking.repository;

import com.group2.parking.entity.ParkingBuilding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingBuildingRepository extends JpaRepository<ParkingBuilding, Integer> {
    List<ParkingBuilding> findByStatus(String status);
}
