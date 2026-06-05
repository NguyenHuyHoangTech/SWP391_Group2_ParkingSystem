package com.group2.parking.controller;

import com.group2.parking.dto.response.ApiResponse;
import com.group2.parking.dto.request.FloorRequest;
import com.group2.parking.dto.response.FloorResponse;
import com.group2.parking.service.FloorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/floors")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class FloorController {

    private final FloorService floorService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<FloorResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(floorService.getAllFloors()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FloorResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(floorService.getFloorById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FloorResponse>> create(@RequestBody FloorRequest req) {
        return ResponseEntity.status(201).body(ApiResponse.ok(floorService.createFloor(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FloorResponse>> update(
            @PathVariable Integer id, @RequestBody FloorRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(floorService.updateFloor(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Integer id) {
        floorService.deleteFloor(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa tầng thành công"));
    }
}
