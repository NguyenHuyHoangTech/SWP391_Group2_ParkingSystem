package com.group2.parking.controller;

import com.group2.parking.dto.request.ZoneRequest;
import com.group2.parking.dto.response.ApiResponse;
import com.group2.parking.dto.response.ZoneResponse;
import com.group2.parking.service.ZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
public class ZoneController {

    private final ZoneService zoneService;

    // PBMS-9: Xem – tất cả actor
    @GetMapping
    public ResponseEntity<ApiResponse<List<ZoneResponse>>> getZones(
            @RequestParam(required = false) Integer floorId) {
        List<ZoneResponse> zones = (floorId != null)
                ? zoneService.getZonesByFloor(floorId)
                : zoneService.getAllZones();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khu vực thành công", zones));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ZoneResponse>> getZoneById(@PathVariable Integer id) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy thông tin khu vực thành công", zoneService.getZoneById(id)));
    }

    // PBMS-10: Thêm mới – ADMIN only
    @PostMapping
    public ResponseEntity<ApiResponse<ZoneResponse>> createZone(
            @RequestHeader(value = "X-Role", defaultValue = "") String role,
            @Valid @RequestBody ZoneRequest request) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Chỉ ADMIN mới có quyền thêm khu vực"));
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Thêm khu vực thành công", zoneService.createZone(request)));
    }

    // PBMS-11: Cập nhật – ADMIN only
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ZoneResponse>> updateZone(
            @PathVariable Integer id,
            @RequestHeader(value = "X-Role", defaultValue = "") String role,
            @Valid @RequestBody ZoneRequest request) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Chỉ ADMIN mới có quyền cập nhật khu vực"));
        }
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật khu vực thành công", zoneService.updateZone(id, request)));
    }

    // Xóa Zone – ADMIN only, chặn nếu có Slot
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteZone(
            @PathVariable Integer id,
            @RequestHeader(value = "X-Role", defaultValue = "") String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Chỉ ADMIN mới có quyền xóa khu vực"));
        }
        zoneService.deleteZone(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa khu vực thành công", null));
    }
}
