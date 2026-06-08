package com.group2.parking.repository;

import com.group2.parking.entity.ParkingBuilding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Repository provides ParkingBuilding lookup methods used by staff validation and building filters.
@Repository
public interface ParkingBuildingRepository extends JpaRepository<ParkingBuilding, Integer> {
    // Derived query returns buildings matching an operational status such as OPEN or CLOSED.
    List<ParkingBuilding> findByStatus(String status);
}
