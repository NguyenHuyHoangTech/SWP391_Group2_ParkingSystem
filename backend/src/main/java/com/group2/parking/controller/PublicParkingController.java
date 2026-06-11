package com.group2.parking.controller;

import com.group2.parking.dto.response.BuildingResponse;
import com.group2.parking.dto.response.CapacityResponse;
import com.group2.parking.service.ParkingPublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicParkingController {

    private final ParkingPublicService parkingPublicService;

    /*
    --- UC-403: XEM SỨC CHỨA HIỆN TẠI CỦA BÃI XE ---
     */
    @GetMapping("/buildings/{buildingId}/capacity")
    public CapacityResponse getLiveCapacity(
            @PathVariable Integer buildingId,
            @RequestParam Integer vehicleTypeId) {
        return parkingPublicService.getLiveCapacity(buildingId, vehicleTypeId);
    }

    /*
    --- UC-404 PULIC XEM DANH SÁCH BÃI XE ĐANG OPEN ---
     */
    @GetMapping("/buildings")
    public List<BuildingResponse> getOpenBuildings() {
        return parkingPublicService.getOpenBuildings();
    }

    //UC-404 PUCLIC XEM CHI TIẾT 1 BÃI XE
    // GET /api/public/buildings/1
    @GetMapping("/buildings/{buildingId}")
    public BuildingResponse getBuilding(@PathVariable Integer buildingId) {
        return parkingPublicService.getBuildingDetail(buildingId);
    }
}
