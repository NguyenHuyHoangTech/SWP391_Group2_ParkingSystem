package com.group2.parking.service;

import com.group2.parking.dto.PricingBlockDTO;
import com.group2.parking.dto.PricingPolicyDTO;
import com.group2.parking.entity.PricingBlock;
import com.group2.parking.entity.PricingPolicy;
import com.group2.parking.entity.VehicleType;
import com.group2.parking.repository.PricingPolicyRepository;
import com.group2.parking.repository.VehicleTypeRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;



@Service
public class PricingPolicyServiceImpl implements PricingPolicyService {

@Autowired // dùng để gọi đối tượng của một class khác ngắn gọn hơn
private PricingPolicyRepository policyRepository;

@Autowired
private VehicleTypeRepository vehicleTypeRepository;

    @Override
    public List<PricingPolicy> getAllPolicies() {
        return policyRepository.getList();
    }

    // THÊM DÒNG NÀY ĐỂ TƯƠNG TÁC VỚI DB


    // 2. TẠO MỚI / CẬP NHẬT
    @Override
    @Transactional
    public PricingPolicy createPolicy(PricingPolicyDTO policyDTO) {
        // Tìm xe
        Integer idLoaiXe = policyDTO.getVehicleTypeId();
        VehicleType xeTimThay = vehicleTypeRepository.findById(idLoaiXe);

        if (xeTimThay == null) {
            throw new RuntimeException("Lỗi: Không tìm thấy loại xe có ID là " + idLoaiXe);
        }


        // Tạo policy
        PricingPolicy chinhSachMoi = new PricingPolicy();
        chinhSachMoi.setName(policyDTO.getName());
        chinhSachMoi.setStatus(policyDTO.getStatus());
        chinhSachMoi.setVehicleType(xeTimThay);

        // Map list block
        List<PricingBlock> danhSachBlock = new ArrayList<>();
        if (policyDTO.getBlocks() != null) {
            for (PricingBlockDTO blockDTO : policyDTO.getBlocks()) {
              PricingBlock blockThat = new PricingBlock();
                blockThat.setBlockOrder(blockDTO.getBlockOrder());
                blockThat.setDurationHours(blockDTO.getDurationHours());
                blockThat.setPrice(blockDTO.getPrice());

                blockThat.setPricingPolicy(chinhSachMoi);
                danhSachBlock.add(blockThat);
            }
        }
        chinhSachMoi.setBlocks(danhSachBlock);

        // Gọi hàm getPolicies (hàm lưu) em đã viết ở repo
        return policyRepository.getPolicies(chinhSachMoi);
    }

    @Override
    public PricingPolicy getPricingPolicies(Integer id) {
        return policyRepository.findId(id);
    }

    @Override
    public void deletePolicies(Integer id) {
         policyRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PricingPolicy updatePolicy(Integer id, PricingPolicyDTO dto) {
        // lấy chính sách cũ theo ID
        PricingPolicy policy = policyRepository.findId(id);

        if (policy == null) throw new RuntimeException("No pricing policy with ID is found: " + id);
        // 2.tìm loại xe mới
        Integer vehicleID = dto.getVehicleTypeId();
        VehicleType carFound = vehicleTypeRepository.findById(vehicleID);
        if(carFound == null) throw new RuntimeException("No vehicle type with ID is found: " + vehicleID);

        //3. ghi đè thông tin cơ bản mới lên
        policy.setName(dto.getName());
        policy.setStatus(dto.getStatus());
        policy.setVehicleType(carFound);

        //4. Xử lý phần Block giá (Xóa sạch block cũ đi, nạp block mới vào chính sách)
        if(policy.getBlocks() != null){
            policy.getBlocks().clear();
        }else{
            policy.setBlocks(new ArrayList<>());
        }
        // Vòng lặp map list block mới (như create)
        if(dto.getBlocks()!= null){
            for(PricingBlockDTO blockDTO : dto.getBlocks()){
                PricingBlock block = new PricingBlock();
                block.setBlockOrder(blockDTO.getBlockOrder());
                block.setDurationHours(blockDTO.getDurationHours());
                block.setPrice(blockDTO.getPrice());
                block.setPricingPolicy(policy);
            }
        }
    return policyRepository.getPolicies(policy);
    }
}
