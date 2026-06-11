package com.group2.parking.repository;

import com.group2.parking.entity.VehicleType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class VehicleTypeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public VehicleType findById(Integer id){

        return entityManager.find(VehicleType.class, id);
    }
}
