/* Lớp Controller xử lý các HTTP request liên quan đến quản lý ô đỗ xe (Slot).
   Cung cấp các API để xem danh sách, thay đổi trạng thái, tạo hàng loạt và xóa ô đỗ.
   Base URL: /api/slots */

package com.group2.parking.controller;

import com.group2.parking.dto.request.BulkGenerateSlotRequest;
import com.group2.parking.dto.response.ApiResponse;
import com.group2.parking.dto.response.BulkGenerateResult;
import com.group2.parking.dto.response.SlotResponse;
import com.group2.parking.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    // Lấy danh sách ô đỗ xe theo khu vực (zoneId là tham số bắt buộc)
    /* Nhận zoneId từ query param
       Gọi slotService.getSlotsByZone(zoneId)
       Trả về danh sách SlotResponse của khu vực đó */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SlotResponse>>> getByZone(
            @RequestParam Integer zoneId) {
        return ResponseEntity.ok(ApiResponse.ok(slotService.getSlotsByZone(zoneId)));
    }

    // Tạo hàng loạt ô đỗ cho một khu vực theo quy tắc đặt tên tự động
    /* Nhận BulkGenerateSlotRequest (zoneId, tiền tố, số lượng, số bắt đầu)
       Gọi slotService.bulkGenerate(req)
       Trả về BulkGenerateResult: số đã tạo, số bị bỏ qua, danh sách tên */
    @PostMapping("/bulk-generate")
    public ResponseEntity<ApiResponse<BulkGenerateResult>> bulkGenerate(
            @RequestBody BulkGenerateSlotRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(slotService.bulkGenerate(req)));
    }

    // Khóa ô đỗ để bảo trì (EMPTY → MAINTENANCE)
    /* Nhận id ô đỗ cần khóa
       Gọi slotService.lockSlot(id)
       Trả về SlotResponse với trạng thái mới hoặc lỗi nếu ô đang có xe */
    @PatchMapping("/{id}/lock")
    public ResponseEntity<ApiResponse<SlotResponse>> lock(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(slotService.lockSlot(id)));
    }

    // Mở ô đỗ sau bảo trì (MAINTENANCE → EMPTY)
    /* Nhận id ô đỗ cần mở
       Gọi slotService.unlockSlot(id)
       Trả về SlotResponse với trạng thái EMPTY hoặc lỗi nếu ô không ở trạng thái MAINTENANCE */
    @PatchMapping("/{id}/unlock")
    public ResponseEntity<ApiResponse<SlotResponse>> unlock(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(slotService.unlockSlot(id)));
    }

    // Đánh dấu xe vào đỗ thủ công (EMPTY → OCCUPIED)
    // Dùng tạm trước khi tích hợp module check-in thật
    /* Nhận id ô đỗ
       Gọi slotService.occupySlot(id)
       Trả về SlotResponse với trạng thái OCCUPIED hoặc lỗi nếu ô không trống */
    @PatchMapping("/{id}/occupy")
    public ResponseEntity<ApiResponse<SlotResponse>> occupy(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(slotService.occupySlot(id)));
    }

    // Đánh dấu xe ra khỏi ô (OCCUPIED → EMPTY)
    // Dùng tạm trước khi tích hợp module check-out thật
    /* Nhận id ô đỗ
       Gọi slotService.vacateSlot(id)
       Trả về SlotResponse với trạng thái EMPTY hoặc lỗi nếu ô không có xe */
    @PatchMapping("/{id}/vacate")
    public ResponseEntity<ApiResponse<SlotResponse>> vacate(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(slotService.vacateSlot(id)));
    }

    // Xóa một ô đỗ xe theo id
    /* Nhận id ô đỗ cần xóa
       Gọi slotService.deleteSlot(id)
       Trả về thông báo thành công hoặc lỗi 404 nếu không tìm thấy */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Integer id) {
        slotService.deleteSlot(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa ô đỗ thành công"));
    }

    // Xóa nhiều ô đỗ cùng lúc theo danh sách id
    /* Nhận danh sách id (List<Integer>) từ request body
       Gọi slotService.bulkDeleteSlots(ids)
       Trả về thông báo kèm số lượng ô đã xóa thực tế */
    @DeleteMapping("/bulk")
    public ResponseEntity<ApiResponse<String>> bulkDelete(@RequestBody List<Integer> ids) {
        int count = slotService.bulkDeleteSlots(ids);
        return ResponseEntity.ok(ApiResponse.ok("Xóa thành công " + count + " ô đỗ"));
    }
}
