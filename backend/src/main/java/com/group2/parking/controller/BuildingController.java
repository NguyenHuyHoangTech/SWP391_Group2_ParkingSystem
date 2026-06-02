package com.group2.parking.controller;

import com.group2.parking.dto.ApiResponse;
import com.group2.parking.entity.ParkingBuilding;
import com.group2.parking.repository.ParkingBuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buildings")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class BuildingController {

    private final ParkingBuildingRepository buildingRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ParkingBuilding>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(buildingRepository.findAll()));
    }
}
