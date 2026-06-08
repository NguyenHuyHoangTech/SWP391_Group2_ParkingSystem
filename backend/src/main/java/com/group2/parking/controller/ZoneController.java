/* Lớp Controller xử lý các HTTP request liên quan đến quản lý khu vực đỗ xe (Zone).
   Nhận yêu cầu từ client, ủy quyền xử lý cho ZoneService và trả về phản hồi JSON.
   Base URL: /api/zones */

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

    // Lấy danh sách khu vực — có thể lọc theo tầng nếu truyền floorId
    /* Nếu floorId có giá trị → gọi getZonesByFloor(floorId)
       Nếu không có floorId   → gọi getAllZones()
       Trả về danh sách ZoneResponse dạng JSON */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ZoneResponse>>> getAll(
            @RequestParam(required = false) Integer floorId) {
        List<ZoneResponse> data = (floorId != null)
                ? zoneService.getZonesByFloor(floorId)
                : zoneService.getAllZones();
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    // Lấy thông tin một khu vực theo id
    /* Gọi zoneService.getZoneById(id)
       Trả về ZoneResponse tương ứng hoặc lỗi 404 nếu không tìm thấy */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ZoneResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(zoneService.getZoneById(id)));
    }

    // Tạo mới một khu vực đỗ xe
    /* Nhận ZoneRequest từ client (tên, sức chứa, tầng)
       Gọi zoneService.createZone(req)
       Trả về ZoneResponse vừa tạo với HTTP 201 Created */
    @PostMapping
    public ResponseEntity<ApiResponse<ZoneResponse>> create(@RequestBody ZoneRequest req) {
        return ResponseEntity.status(201).body(ApiResponse.ok(zoneService.createZone(req)));
    }

    // Cập nhật thông tin khu vực theo id
    /* Nhận id khu vực và ZoneRequest mới từ client
       Gọi zoneService.updateZone(id, req)
       Trả về ZoneResponse đã cập nhật hoặc lỗi nếu vi phạm sức chứa */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ZoneResponse>> update(
            @PathVariable Integer id, @RequestBody ZoneRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(zoneService.updateZone(id, req)));
    }

    // Xóa một khu vực đỗ xe theo id (cascade: xóa toàn bộ ô đỗ bên trong trước)
    /* Nhận id khu vực cần xóa
       Gọi zoneService.deleteZone(id) — tự động xóa slot trước, rồi xóa zone
       Trả về thông báo thành công */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Integer id) {
        zoneService.deleteZone(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa khu vực thành công"));
    }
}
