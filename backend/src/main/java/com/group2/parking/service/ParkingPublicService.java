package com.group2.parking.service;

import com.group2.parking.dto.BuildingResponse;
import com.group2.parking.entity.ParkingBuilding;
import com.group2.parking.repository.ParkingBuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingPublicService {

    private final ParkingBuildingRepository parkingBuildingRepo;

    //UC-404: XEM DANH SÁCH CÁC BÃI XE ĐANG MỞ
    //KO THẤY BÃI CLOSED TRONG DANH SÁCH
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
                .orElseThrow(()-> new ResponseStatusException(
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
