package com.group2.parking.repository;

import com.group2.parking.entity.PricingPolicy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional // do dùng persist and merge
public class PricingPolicyRepository {
    // Công cụ giúp tương tác với Database
    @PersistenceContext
    private EntityManager entityManager;

// lấy danh sách
    public List<PricingPolicy> getList(){
        String sql = "SELECT p FROM PricingPolicy p";

       return entityManager.createQuery(sql, PricingPolicy.class).getResultList();

    }

    // dùng lưu dữ liệu
    public PricingPolicy getPolicies(PricingPolicy policy){
        if(policy.getId() == null){
            // insert
            entityManager.persist(policy);
            return policy;
        }else{
          return entityManager.merge(policy);
        }
    }
// hàm tìm ID
    public PricingPolicy findId(Integer id){
        // Nó tự động tạo lệnh SELECT * FROM PricingPolicy WHERE id = ? tìm kiếm ID
        return entityManager.find(PricingPolicy.class, id);
    }

    public void deleteById(Integer id){
        PricingPolicy policy = entityManager.find(PricingPolicy.class, id);
        if(id != null){
          entityManager.remove(policy);
        }
    }
}
