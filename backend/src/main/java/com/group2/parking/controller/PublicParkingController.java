package com.group2.parking.controller;

import com.group2.parking.dto.BuildingResponse;
import com.group2.parking.service.ParkingPublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class PublicParkingController {

    private final ParkingPublicService parkingPublicService;

    //UC-404 PULIC XEM DANH SÁCH BÃI XE ĐANG OPEN
    // GET /api/public/buildings
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
