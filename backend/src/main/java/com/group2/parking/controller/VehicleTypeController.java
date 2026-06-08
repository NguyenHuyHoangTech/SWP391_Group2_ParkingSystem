package com.group2.parking.controller;

import com.group2.parking.dto.response.ApiResponse;
import com.group2.parking.entity.VehicleType;
import com.group2.parking.repository.VehicleTypeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle-types")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class VehicleTypeController {

    private final VehicleTypeJpaRepository vehicleTypeRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleType>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(vehicleTypeRepository.findAll()));
    }
}
