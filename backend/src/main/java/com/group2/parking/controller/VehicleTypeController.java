package com.group2.parking.controller;

import com.group2.parking.dto.response.ApiResponse;
import com.group2.parking.dto.response.VehicleTypeResponse;
import com.group2.parking.service.VehicleTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle-types")
@RequiredArgsConstructor
public class VehicleTypeController {

    private final VehicleTypeService vehicleTypeService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleTypeResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy danh sách loại xe thành công",
                        vehicleTypeService.getAllVehicleTypes()));
    }
}
