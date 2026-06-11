package com.group2.parking.service;

import com.group2.parking.dto.response.FeeEstimateResponse;
import com.group2.parking.dto.response.PricingBlockResponse;
import com.group2.parking.dto.response.PricingResponse;
import com.group2.parking.entity.ParkingSession;
import com.group2.parking.entity.PricingBlock;
import com.group2.parking.entity.PricingPolicy;
import com.group2.parking.repository.ParkingSessionRepository;
import com.group2.parking.repository.PricingBlockRepository;
import com.group2.parking.repository.PublicPricingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final PublicPricingRepository publicPricingRepository;
    private final PricingBlockRepository pricingBlockRepository;
    private final ParkingSessionRepository parkingSessionRepository;

    // UC-405: LẤY DANH SÁCH BẢNG GIÁ ĐANG "ACTIVE" CHO PUBLIC XEM
    public List<PricingResponse> getPublicPricing() {
        return publicPricingRepository.findByStatus("ACTIVE")
                .stream()
                .map(this::toPricingResponse)
                .toList();
    }

    // CONVERT PricingPolicy Entity SANG DTO ĐỂ KHÔNG RETURN TRỰC TIẾP Entity ra FE.
    private PricingResponse toPricingResponse(PricingPolicy policy) {
        List<PricingBlockResponse> blocks = pricingBlockRepository
                .findByPricingPolicy_IdOrderByBlockOrderAsc(policy.getId())
                .stream()
                .map(this::toPricingBlockResponse)
                .toList();

        return new PricingResponse(
                policy.getId(),
                policy.getName(),
                policy.getVehicleType() != null ? policy.getVehicleType().getId() : null,
                policy.getVehicleType() != null ? policy.getVehicleType().getName() : null,
                policy.getStatus(),
                blocks
        );
    }

    // CONVERT MỚI BLOCK GIÁ SANG DTO, GIỮ ĐÚNG THỨ TỰ VÀ SỐ TIỀN.
    private PricingBlockResponse toPricingBlockResponse(PricingBlock block) {
        return new PricingBlockResponse(
                block.getBlockOrder(),
                block.getDurationHours(),
                block.getPrice()
        );
    }

    //UC-406: TRA CỨU PHÍ
    public FeeEstimateResponse estimateFeeByLicensePlate(String licensePlate){

        //1. CHUẨN HÓA LẠI LICENSE PLATE
        String normalLicensePlate = licensePlate.trim().toUpperCase();

        //2. TÌM PHIÊN GỬI XE ĐANG "ACTIVE"
        ParkingSession session = parkingSessionRepository.findFirstByLicensePlateAndStatus(normalLicensePlate, "ACTIVE")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xe đang gửi với biển số: " + normalLicensePlate));

        //3. CHECK XE ĐÃ CÓ GIỜ VÀO BÃI CHƯA
        if (session.getCheckInTime() == null) {
            throw new RuntimeException("Phiên gửi xe chưa có thời gian check-in!!!");
        }

        //4. TÍNH THỜI GIAN ĐÃ GỬI XE
        LocalDateTime now = LocalDateTime.now();
        long parkingMinutes = ChronoUnit.MINUTES.between(session.getCheckInTime(), now);

        //NẾU CHECK-IN Ở TƯƠNG LAI
        if (parkingMinutes <= 0) {
            throw new RuntimeException("Thời gian check-in không hợp lệ!!!");
        }

        //5. TÌM BẢNG GIÁ ACTIVE THEO LOẠI XE CỦA SESSION
        PricingPolicy policy = publicPricingRepository.findFirstByVehicleType_IdAndStatus(session.getVehicleTypeId(), "ACTIVE").orElseThrow(() -> new RuntimeException("KHÔNG TÌM THẤY BẢNG GIÁ CHO LOẠI XE NÀY"));

        //6. LẤY DANH SÁCH BLOCK GIÁ THEO POLICY, SORT THEO BLOCK_ORDER TĂNG DẦN
        List<PricingBlock> block = pricingBlockRepository.findByPricingPolicy_IdOrderByBlockOrderAsc(policy.getId());

        if (block.isEmpty()){
            throw new RuntimeException("Bảng giá chưa có block tính tiền");
        }

        //7.TÍNH PHÍ TẠM TÍNH
        BigDecimal estimatedFee = calculateFee(parkingMinutes, block);

        //8. TRẢ DTO RESPONE CHO CONTROLLER
        return new FeeEstimateResponse(
                normalLicensePlate,
                parkingMinutes,
                estimatedFee
        );
    }

    //HELPER: Tính phí theo các block giá.
    private BigDecimal calculateFee(long parkingMinutes, List<PricingBlock> blocks) {

        // Nếu gửi dưới 1 phút hoặc vừa check-in, vẫn tính là 1 giờ/block đầu tiên
        long parkingHours = (long) Math.ceil(parkingMinutes / 60.0);

        if (parkingHours <= 0) {
            parkingHours = 1;
        }

        long remainingHours = parkingHours;
        BigDecimal totalFee = BigDecimal.ZERO;

        for (PricingBlock block : blocks) {
            if (remainingHours <= 0) {
                break;
            }

            // Chỉ cần xe vào block nào thì tính giá block đó
            totalFee = totalFee.add(block.getPrice());

            // Trừ số giờ mà block này bao phủ
            remainingHours -= block.getDurationHours();
        }
        return totalFee;
    }
}
