package com.group2.parking.repository;

import com.group2.parking.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlotRepository extends JpaRepository<Slot, Integer> {
    boolean existsByZoneId(Integer zoneId);
}
