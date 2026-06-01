package com.group2.parking.service;

import com.group2.parking.dto.PricingPolicyDTO;
import com.group2.parking.entity.PricingPolicy;

import java.util.List;
// tạo chính sách giá mới
public interface PricingPolicyService {
    List<PricingPolicy> getAllPolicies();
    PricingPolicy createPolicy(PricingPolicyDTO policyDTO);
    PricingPolicy getPricingPolicies(Integer id);
    void deletePolicies(Integer id);
}
