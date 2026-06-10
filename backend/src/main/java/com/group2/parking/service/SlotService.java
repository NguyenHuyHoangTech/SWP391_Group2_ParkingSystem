/* Lớp Service chứa toàn bộ logic nghiệp vụ quản lý ô đỗ xe (Slot).
   Xử lý các nghiệp vụ: xem danh sách, thay đổi trạng thái ô đỗ,
   tạo hàng loạt ô đỗ tự động theo quy tắc đặt tên, xóa đơn lẻ và xóa hàng loạt. */

package com.group2.parking.service;

import com.group2.parking.dto.request.BulkGenerateSlotRequest;
import com.group2.parking.dto.response.BulkGenerateResult;
import com.group2.parking.dto.response.SlotResponse;
import com.group2.parking.entity.ParkingZone;
import com.group2.parking.entity.Slot;
import com.group2.parking.exception.AppException;
import com.group2.parking.repository.ParkingZoneRepository;
import com.group2.parking.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SlotService {

    private final SlotRepository slotRepository;
    private final ParkingZoneRepository zoneRepository;

    // Lấy danh sách ô đỗ theo khu vực (zoneId)
    /* Tìm zone theo zoneId — ném lỗi 404 nếu không tồn tại
       Truy vấn danh sách slot theo zoneId
       Chuyển đổi từng Slot thành SlotResponse (kèm thông tin zone)
       Trả về danh sách kết quả */
    public List<SlotResponse> getSlotsByZone(Integer zoneId) {
        ParkingZone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy khu vực với id: " + zoneId));

        return slotRepository.findByZoneId(zoneId).stream()
                .map(slot -> toResponse(slot, zone))
                .collect(Collectors.toList());
    }

    // Khóa ô đỗ để bảo trì (EMPTY → MAINTENANCE)
    /* Tìm slot theo slotId — ném lỗi 404 nếu không tồn tại
       Nếu slot đang OCCUPIED → ném lỗi 400 (không thể khóa khi có xe)
       Nếu slot đã MAINTENANCE → ném lỗi 400 (đã ở trạng thái này rồi)
       Cập nhật status = MAINTENANCE và lưu lại
       Trả về SlotResponse với trạng thái mới */
    public SlotResponse lockSlot(Integer slotId) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy ô đỗ với id: " + slotId));

        if ("OCCUPIED".equals(slot.getStatus())) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Không thể khóa ô \"" + slot.getName() + "\" vì hiện có xe đang đỗ!");
        }
        if ("MAINTENANCE".equals(slot.getStatus())) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Ô \"" + slot.getName() + "\" đã đang ở trạng thái bảo trì!");
        }

        slot.setStatus("MAINTENANCE");
        Slot saved = slotRepository.save(slot);

        ParkingZone zone = zoneRepository.findById(saved.getZoneId()).orElse(null);
        return toResponse(saved, zone);
    }

    // Mở ô đỗ sau khi bảo trì xong (MAINTENANCE → EMPTY)
    /* Tìm slot theo slotId — ném lỗi 404 nếu không tồn tại
       Nếu slot không phải MAINTENANCE → ném lỗi 400 (chỉ mở được slot đang bảo trì)
       Cập nhật status = EMPTY và lưu lại
       Trả về SlotResponse với trạng thái mới */
    public SlotResponse unlockSlot(Integer slotId) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy ô đỗ với id: " + slotId));

        if (!"MAINTENANCE".equals(slot.getStatus())) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Chỉ có thể mở ô đang ở trạng thái bảo trì!");
        }

        slot.setStatus("EMPTY");
        Slot saved = slotRepository.save(slot);

        ParkingZone zone = zoneRepository.findById(saved.getZoneId()).orElse(null);
        return toResponse(saved, zone);
    }

    // Đánh dấu xe vào đỗ thủ công (EMPTY → OCCUPIED)
    // Dùng tạm trước khi tích hợp module check-in thật
    /* Tìm slot theo slotId — ném lỗi 404 nếu không tồn tại
       Nếu slot không phải EMPTY → ném lỗi 400 (chỉ đánh dấu được ô đang trống)
       Cập nhật status = OCCUPIED và lưu lại
       Trả về SlotResponse với trạng thái mới */
    public SlotResponse occupySlot(Integer slotId) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy ô đỗ với id: " + slotId));

        if (!"EMPTY".equals(slot.getStatus())) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Chỉ có thể đánh dấu ô đang ở trạng thái trống!");
        }

        slot.setStatus("OCCUPIED");
        Slot saved = slotRepository.save(slot);

        ParkingZone zone = zoneRepository.findById(saved.getZoneId()).orElse(null);
        return toResponse(saved, zone);
    }

    // Đánh dấu xe ra khỏi ô đỗ (OCCUPIED → EMPTY)
    // Dùng tạm trước khi tích hợp module check-out thật
    /* Tìm slot theo slotId — ném lỗi 404 nếu không tồn tại
       Nếu slot không phải OCCUPIED → ném lỗi 400 (chỉ bỏ đánh dấu ô đang có xe)
       Cập nhật status = EMPTY và lưu lại
       Trả về SlotResponse với trạng thái mới */
    public SlotResponse vacateSlot(Integer slotId) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy ô đỗ với id: " + slotId));

        if (!"OCCUPIED".equals(slot.getStatus())) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Chỉ có thể bỏ đánh dấu ô đang có xe!");
        }

        slot.setStatus("EMPTY");
        Slot saved = slotRepository.save(slot);

        ParkingZone zone = zoneRepository.findById(saved.getZoneId()).orElse(null);
        return toResponse(saved, zone);
    }

    // Tạo hàng loạt ô đỗ xe cho một khu vực theo quy tắc đặt tên tự động
    /* Tìm zone theo zoneId — ném lỗi 404 nếu không tồn tại
       Kiểm tra count phải lớn hơn 0, prefix không được trống — ném lỗi 400 nếu vi phạm
       Đếm số slot đã có trong zone, tính remaining = capacity - đã có
       Nếu count > remaining → ném lỗi 400 (vượt sức chứa zone)
       Lấy tập hợp tên slot đã tồn tại để kiểm tra trùng
       Tính độ dài pad số thứ tự (ví dụ 12 slot → pad 2 chữ số: 01, 02)
       Lặp từ startFrom đến startFrom + count - 1:
           Tạo tên slot = prefix + số thứ tự có padding (VD: "A1-01")
           Nếu tên đã tồn tại → thêm vào danh sách skippedNames
           Ngược lại → tạo Slot mới (status = EMPTY) và thêm vào danh sách toSave
       Lưu tất cả slot mới vào database (saveAll)
       Trả về BulkGenerateResult: số đã tạo, số bỏ qua, danh sách tên */
    public BulkGenerateResult bulkGenerate(BulkGenerateSlotRequest req) {
        ParkingZone zone = zoneRepository.findById(req.getZoneId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy khu vực với id: " + req.getZoneId()));

        if (req.getCount() == null || req.getCount() < 1) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Số lượng phải lớn hơn 0!");
        }
        if (req.getPrefix() == null || req.getPrefix().isBlank()) {
            throw new AppException(HttpStatus.BAD_REQUEST, "Tiền tố không được để trống!");
        }

        // Kiểm tra còn đủ chỗ trong zone không
        long existingCount = slotRepository.countByZoneId(req.getZoneId());
        long remaining = zone.getCapacity() - existingCount;
        if (req.getCount() > remaining) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Khu vực \"" + zone.getName() + "\" chỉ còn " + remaining
                            + " chỗ (capacity: " + zone.getCapacity()
                            + ", đã có: " + existingCount + " ô đỗ)");
        }

        // Lấy tập tên slot đã tồn tại trong zone để check trùng
        Set<String> existingNames = slotRepository.findByZoneId(req.getZoneId())
                .stream().map(Slot::getName).collect(Collectors.toSet());

        int startFrom = (req.getStartFrom() != null && req.getStartFrom() >= 1) ? req.getStartFrom() : 1;
        String prefix  = req.getPrefix().trim();
        // Tính số chữ số cần pad (đủ để cover startFrom + count)
        int maxNum  = startFrom + req.getCount() - 1;
        int padLen  = String.valueOf(maxNum).length();
        String fmt  = "%0" + padLen + "d";

        List<String> createdNames = new ArrayList<>();
        List<String> skippedNames = new ArrayList<>();
        List<Slot>   toSave       = new ArrayList<>();

        for (int i = 0; i < req.getCount(); i++) {
            String slotName = prefix + String.format(fmt, startFrom + i);
            if (existingNames.contains(slotName)) {
                skippedNames.add(slotName);
            } else {
                toSave.add(Slot.builder()
                        .zoneId(req.getZoneId())
                        .name(slotName)
                        .status("EMPTY")
                        .build());
                createdNames.add(slotName);
            }
        }

        slotRepository.saveAll(toSave);

        return BulkGenerateResult.builder()
                .created(createdNames.size())
                .skipped(skippedNames.size())
                .createdNames(createdNames)
                .skippedNames(skippedNames)
                .build();
    }

    // Xóa một ô đỗ xe theo id
    /* Tìm slot theo id — ném lỗi 404 nếu không tồn tại
       Nếu slot đang OCCUPIED (có xe) → ném lỗi 400 (không được phép xóa khi có xe)
       Xóa slot khỏi database */
    public void deleteSlot(Integer id) {
        Slot slot = slotRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy ô đỗ với id: " + id));
        if ("OCCUPIED".equals(slot.getStatus())) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Không thể xóa ô \"" + slot.getName() + "\" vì hiện có xe đang đỗ!");
        }
        slotRepository.deleteById(id);
    }

    // Xóa nhiều ô đỗ cùng lúc theo danh sách id
    /* Truy vấn danh sách slot thực sự tồn tại trong danh sách id
       Kiểm tra nếu có bất kỳ slot nào đang OCCUPIED → ném lỗi 400 kèm danh sách tên
       Xóa tất cả slot hợp lệ và trả về số lượng đã xóa */
    public int bulkDeleteSlots(List<Integer> ids) {
        List<Slot> slots = slotRepository.findAllById(ids);
        List<String> occupiedNames = slots.stream()
                .filter(s -> "OCCUPIED".equals(s.getStatus()))
                .map(Slot::getName)
                .collect(Collectors.toList());
        if (!occupiedNames.isEmpty()) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Không thể xóa vì các ô sau đang có xe: " + String.join(", ", occupiedNames));
        }
        List<Integer> existing = slots.stream().map(Slot::getId).collect(Collectors.toList());
        slotRepository.deleteAllById(existing);
        return existing.size();
    }

    // Chuyển đổi entity Slot thành SlotResponse kèm thông tin zone, tầng, tòa nhà
    /* Nếu zone không null:
           Lấy tên zone
           Nếu zone có tầng → lấy tên tầng
               Nếu tầng có tòa nhà → lấy tên tòa nhà
       Xây dựng SlotResponse với đầy đủ thông tin và trả về */
    private SlotResponse toResponse(Slot slot, ParkingZone zone) {
        String zoneName = null, floorName = null, buildingName = null;

        if (zone != null) {
            zoneName = zone.getName();
            if (zone.getFloor() != null) {
                floorName = zone.getFloor().getName();
                if (zone.getFloor().getBuilding() != null) {
                    buildingName = zone.getFloor().getBuilding().getName();
                }
            }
        }

        return SlotResponse.builder()
                .id(slot.getId())
                .name(slot.getName())
                .status(slot.getStatus())
                .zoneId(slot.getZoneId())
                .zoneName(zoneName)
                .floorName(floorName)
                .buildingName(buildingName)
                .build();
    }
}
