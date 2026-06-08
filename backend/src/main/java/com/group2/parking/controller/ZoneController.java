package com.group2.parking.controller;

import com.group2.parking.dto.response.ApiResponse;
import com.group2.parking.dto.request.ZoneRequest;
import com.group2.parking.dto.response.ZoneResponse;
import com.group2.parking.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ZoneController {

    private final ZoneService zoneService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ZoneResponse>>> getAll(
            @RequestParam(required = false) Integer floorId) {
        List<ZoneResponse> data = (floorId != null)
                ? zoneService.getZonesByFloor(floorId)
                : zoneService.getAllZones();
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ZoneResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(zoneService.getZoneById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ZoneResponse>> create(@RequestBody ZoneRequest req) {
        return ResponseEntity.status(201).body(ApiResponse.ok(zoneService.createZone(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ZoneResponse>> update(
            @PathVariable Integer id, @RequestBody ZoneRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(zoneService.updateZone(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Integer id) {
        zoneService.deleteZone(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa khu vực thành công"));
    }
}
