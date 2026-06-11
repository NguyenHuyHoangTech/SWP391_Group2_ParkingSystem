package com.group2.parking.service;

import com.group2.parking.dto.response.BuildingResponse;
import com.group2.parking.dto.response.CapacityResponse;
import com.group2.parking.entity.ParkingBuilding;
import com.group2.parking.exception.AppException;
import com.group2.parking.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParkingPublicService {

    private final ParkingBuildingRepository parkingBuildingRepo;
    private final VehicleTypeRepository vehicleTypeRepo;
    private final ParkingSessionRepository parkingSessionRepo;
    private final ParkingZoneRepository parkingZoneRepo;
    private final BookingRepository bookingRepo;

    /*
    --- UC-403: TRA CỨU SỨC CHỨA LIVE ---
    --- THEO BÃI XE VÀ PHƯƠNG TIỆN ---
     */
    public CapacityResponse getLiveCapacity(Integer buildingId, Integer vehicleTypeId) {

        /*
         --- Kiểm tra bãi xe có tồn tại. ---
         */
        ParkingBuilding building = parkingBuildingRepo.findById(buildingId)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "Parking building not found"));

        /*
         * TÌM VEHICLE THEO ID
         */
        if (vehicleTypeRepo.findById(vehicleTypeId) == null) {
            throw new AppException(
                    HttpStatus.NOT_FOUND,
                    "Vehicle type not found"
            );
        }

        // DÙNG THỜI GIAN CHUNG ĐỂ TÍNH BOOKING
        LocalDateTime now = LocalDateTime.now();

        // TỔNG SỨC CHỨA
        long totalSlots = parkingZoneRepo.sumCapacity(buildingId, vehicleTypeId);

        // CHỈ SESSION ACTIVE MỚI LÀ XE ĐANG GỬI
        long occupiedSlots = parkingSessionRepo.countByBuildingIdAndVehicleTypeIdAndStatus(
                        buildingId,
                        vehicleTypeId,
                        "ACTIVE"
                );

        //ĐẾM BOOKING ĐANG GIỮ CHỖ TẠI THỜI ĐIỂM HIỆN TẠI
        long activeBookings = bookingRepo.countByBuildingIdAndVehicleTypeIdAndStatusAndCheckedInAtIsNullAndExpectedCheckinTimeLessThanEqualAndHoldUntilAfter(
                            buildingId,
                            vehicleTypeId,
                            "CONFIRMED",
                            now,
                            now
        );

        // SLOT CÒN TRỐNG KHÔNG BAO GIỜ ÂM
        long availableSlots = Math.max(0, totalSlots - (occupiedSlots + activeBookings));

        // CLOSED vẫn trả kết quả và kèm buildingStatus.
        return new CapacityResponse(
                building.getId(),
                building.getStatus(),
                vehicleTypeId,
                totalSlots,
                occupiedSlots,
                activeBookings,
                availableSlots,
                now
        );
    }


    //UC-404: XEM DANH SÁCH CÁC BÃI XE ĐANG MỞ (KO THẤY BÃI CLOSED TRONG DANH SÁCH)
    public List<BuildingResponse> getOpenBuildings() {
        return parkingBuildingRepo.findByStatus("OPEN")
                .stream()
                .map(this::toBuildingResponse)
                .toList();
    }

    //UC-404: LẤY CHI TIẾT BÃI XE THEO ID
    public BuildingResponse getBuildingDetail(Integer buildingId) {
        ParkingBuilding building = parkingBuildingRepo
                .findById(buildingId)
                .orElseThrow(()-> new AppException(
                        HttpStatus.NOT_FOUND, "Parking building not found"));
        return toBuildingResponse(building);
    }

    //CHUYỂN ENTITY NỘI BỘ THÀNH DTO PUBLIC
    private BuildingResponse toBuildingResponse(ParkingBuilding building) {
        return new BuildingResponse(
                building.getId(),
                building.getName(),
                building.getAddress(),
                building.getStatus()
        );
    }
}
