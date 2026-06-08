/* Lớp Controller xử lý các HTTP request liên quan đến quản lý tầng (Floor).
   Nhận yêu cầu từ client, ủy quyền xử lý cho FloorService và trả về phản hồi JSON.
   Base URL: /api/floors */

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

    // Lấy danh sách tất cả tầng trong hệ thống
    /* Gọi floorService.getAllFloors()
       Trả về danh sách FloorResponse dạng JSON */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FloorResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(floorService.getAllFloors()));
    }

    // Lấy thông tin một tầng theo id
    /* Gọi floorService.getFloorById(id)
       Trả về FloorResponse tương ứng hoặc lỗi 404 nếu không tồn tại */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FloorResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(floorService.getFloorById(id)));
    }

    // Tạo mới một tầng từ dữ liệu request body
    /* Nhận FloorRequest từ client (tên, cấp tầng, sức chứa, tòa nhà, loại xe)
       Gọi floorService.createFloor(req)
       Trả về FloorResponse vừa tạo với HTTP 201 Created */
    @PostMapping
    public ResponseEntity<ApiResponse<FloorResponse>> create(@RequestBody FloorRequest req) {
        return ResponseEntity.status(201).body(ApiResponse.ok(floorService.createFloor(req)));
    }

    // Cập nhật thông tin tầng theo id
    /* Nhận id tầng cần cập nhật và FloorRequest mới từ client
       Gọi floorService.updateFloor(id, req)
       Trả về FloorResponse đã cập nhật hoặc lỗi nếu vi phạm nghiệp vụ */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FloorResponse>> update(
            @PathVariable Integer id, @RequestBody FloorRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(floorService.updateFloor(id, req)));
    }

    // Xóa một tầng theo id
    /* Nhận id tầng cần xóa
       Gọi floorService.deleteFloor(id)
       Trả về thông báo thành công hoặc lỗi nếu tầng còn khu vực bên trong */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Integer id) {
        floorService.deleteFloor(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa tầng thành công"));
    }
}
