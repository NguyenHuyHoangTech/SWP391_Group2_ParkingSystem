package com.group2.parking.repository;

import com.group2.parking.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FloorRepository extends JpaRepository<Floor, Integer> {
    List<Floor> findByBuildingId(Integer buildingId);
    boolean existsByBuildingId(Integer buildingId);
}
