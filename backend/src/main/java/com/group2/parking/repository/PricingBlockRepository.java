package com.group2.parking.repository;

import com.group2.parking.entity.PricingBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PricingBlockRepository extends JpaRepository<PricingBlock, Integer> {

    //LẤY CÁC BLOCK GIÁ THUỘC 1 POLICY VÀ SORT THEO BLOCK_ORDER
    List<PricingBlock> findByPricingPolicy_IdOrderByBlockOrderAsc(Integer policyId);

}
