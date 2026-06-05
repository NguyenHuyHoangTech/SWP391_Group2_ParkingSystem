package com.group2.parking.repository;

import com.group2.parking.entity.PricingPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublicPricingRepository extends JpaRepository<PricingPolicy, Integer> {

    //UC-405: LẤY BẢNG GIÁ ACTIVE CHO PUBLIC XEM
    List<PricingPolicy> findByStatus(String status);
}
