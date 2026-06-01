package com.group2.parking.controller;

import com.group2.parking.dto.request.FloorRequest;
import com.group2.parking.dto.response.ApiResponse;
import com.group2.parking.dto.response.FloorResponse;
import com.group2.parking.service.FloorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/floors")
@RequiredArgsConstructor
public class FloorController {

    private final FloorService floorService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FloorResponse>>> getFloors(
            @RequestParam(required = false) Integer buildingId) {
        List<FloorResponse> floors = (buildingId != null)
                ? floorService.getFloorsByBuilding(buildingId)
                : floorService.getAllFloors();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách tầng thành công", floors));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FloorResponse>> getFloorById(@PathVariable Integer id) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy thông tin tầng thành công", floorService.getFloorById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FloorResponse>> createFloor(
            @RequestHeader(value = "X-Role", defaultValue = "") String role,
            @Valid @RequestBody FloorRequest request) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Chỉ ADMIN mới có quyền thêm tầng"));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Thêm tầng thành công", floorService.createFloor(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FloorResponse>> updateFloor(
            @PathVariable Integer id,
            @RequestHeader(value = "X-Role", defaultValue = "") String role,
            @Valid @RequestBody FloorRequest request) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Chỉ ADMIN mới có quyền cập nhật tầng"));
        }
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật tầng thành công", floorService.updateFloor(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFloor(
            @PathVariable Integer id,
            @RequestHeader(value = "X-Role", defaultValue = "") String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Chỉ ADMIN mới có quyền xóa tầng"));
        }
        floorService.deleteFloor(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa tầng thành công", null));
    }
}
