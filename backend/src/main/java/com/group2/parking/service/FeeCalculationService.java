package com.group2.parking.service;

import com.group2.parking.dto.FeeEstimateResponse;
import com.group2.parking.entity.ParkingSession;
import com.group2.parking.entity.PricingBlock;
import com.group2.parking.entity.PricingPolicy;
import com.group2.parking.repository.ParkingSessionRepository;
import com.group2.parking.repository.PricingPolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class FeeCalculationService {
    @Autowired
    private ParkingSessionRepository sessionRepository;
    @Autowired
    private PricingPolicyRepository policyRepository;

    public FeeEstimateResponse caculateFee(String licensePlate, LocalDateTime checkOutTime){
        //tìm xe và bảng giá
        //Nhận về một cái "Hộp chứa"
        Optional<ParkingSession> sessionOptional = sessionRepository.findFirstByLicensePlateAndStatus(licensePlate, "ACTIVE");
        if(sessionOptional.isEmpty()){ throw new RuntimeException("No vehicle found with this license plate" + licensePlate + " Parked.");}
        // lấy biển số xe để sử dụng
        ParkingSession session = sessionOptional.get();
        // tìm bảng giá
        PricingPolicy policy = policyRepository.findActiveVehicleById(session.getVehicleTypeId());
        if(policy == null) throw new RuntimeException("Pricing policy not found for this vehicle type!");

        // 2. tính tổng giờ đỗ xe
        LocalDateTime checkInTime = session.getCheckInTime();
        if(checkOutTime.isBefore(checkInTime)) {
            throw new RuntimeException("Exit time cannot be earlier than entry time!");
        }
        // tính tổng số phút
        long parkingMinutes  = Duration.between(checkInTime, checkOutTime).toMinutes();

        // thuật toán quy đổi phút ra giờ(chia lấy dư)
        long totalHours = parkingMinutes / 60;
        long phanDu = parkingMinutes % 60;

        if(phanDu > 0){
            totalHours = totalHours + 1; // lố 1 phút cũng tính làm tròng 1 tiếng
        }
        if(totalHours == 0){
            totalHours = 1; // dưới 1 tiếng cũng tính thành là 1 tiếng
        }
        // 3. thuật toán tính tiền(chặt block)
        double totalFee = 0.0;
        long soGioConLai = totalHours;
        List<PricingBlock> blocks = policy.getBlocks();
        // sắp xếp block theo thứ tự 1,2,3
        blocks.sort((b1,b2) -> b1.getBlockOrder  () - b2.getBlockOrder());
        for(int i = 0; i < blocks.size(); i++){
            PricingBlock block = blocks.get(i);
            if(soGioConLai <= 0 ) break;
            totalFee = totalFee + block.getPrice();
            soGioConLai = soGioConLai - block.getDurationHours();
        }

        // 4. Xử lí xe gửi lâu(hết block)
        if(soGioConLai > 0 && blocks != null && !blocks.isEmpty()){
            PricingBlock lastBlock = blocks.get(blocks.size() - 1);
            long soBlockDungThem = soGioConLai / lastBlock.getDurationHours();
            long gioDu = soGioConLai % lastBlock.getDurationHours();

            if(gioDu > 0) {
                soBlockDungThem = soBlockDungThem + 1;
            }
            totalFee = totalFee + (soBlockDungThem * lastBlock.getPrice());
        }

        // 5 Phí phụ qua đêm
        if(checkOutTime.toLocalDate().isAfter(checkInTime.toLocalDate())){
            totalFee = totalFee + 10000;
        }
        // 6. Trả về biên lai
        return new FeeEstimateResponse(licensePlate, parkingMinutes, totalFee);
    }
}
