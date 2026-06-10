/* Lớp Service chứa toàn bộ logic nghiệp vụ quản lý khu vực đỗ xe (ParkingZone).
   Xử lý các nghiệp vụ: tạo, cập nhật, xóa khu vực kèm kiểm tra sức chứa tầng,
   và tổng hợp thống kê số lượng ô đỗ theo trạng thái cho mỗi khu vực. */

package com.group2.parking.service;

import com.group2.parking.dto.request.ZoneRequest;
import com.group2.parking.dto.response.ZoneResponse;
import com.group2.parking.entity.*;
import com.group2.parking.exception.AppException;
import com.group2.parking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZoneService {

    private final ParkingZoneRepository zoneRepository;
    private final FloorRepository floorRepository;
    private final SlotRepository slotRepository;

    // Lấy toàn bộ danh sách khu vực trong hệ thống
    /* Truy vấn tất cả zone trong database
       Chuyển đổi từng ParkingZone thành ZoneResponse (kèm thống kê slot)
       Trả về danh sách kết quả */
    public List<ZoneResponse> getAllZones() {
        return zoneRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Lấy danh sách khu vực theo tầng cụ thể
    /* Truy vấn các zone có floorId tương ứng
       Chuyển đổi từng zone thành ZoneResponse
       Trả về danh sách kết quả */
    public List<ZoneResponse> getZonesByFloor(Integer floorId) {
        return zoneRepository.findByFloorId(floorId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Lấy thông tin một khu vực theo id
    /* Tìm zone theo id trong database
       Nếu không tồn tại → ném ngoại lệ 404 Not Found
       Chuyển đổi thành ZoneResponse và trả về */
    public ZoneResponse getZoneById(Integer id) {
        return toResponse(zoneRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy khu vực với id: " + id)));
    }

    // Tạo mới một khu vực đỗ xe
    /* Tìm tầng theo floorId — ném lỗi 404 nếu không tồn tại
       Lấy loại xe từ tầng (zone thừa hưởng loại xe của tầng)
       Nếu tầng chưa có loại xe → ném lỗi 400
       Tính tổng sức chứa đã dùng của các zone khác trong tầng
       Tính sức chứa còn lại = capacity tầng - đã dùng
       Nếu sức chứa yêu cầu > còn lại → ném lỗi 400
       Tạo ParkingZone mới, lưu vào database và trả về ZoneResponse */
    public ZoneResponse createZone(ZoneRequest req) {
        Floor floor = floorRepository.findById(req.getFloorId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy tầng với id: " + req.getFloorId()));

        VehicleType vehicleType = floor.getVehicleType();
        if (vehicleType == null) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Tầng này chưa được cấu hình loại xe. Hãy cập nhật thông tin tầng trước!");
        }

        int usedCapacity = zoneRepository.findByFloorId(req.getFloorId()).stream()
                .mapToInt(ParkingZone::getCapacity).sum();
        int remaining = floor.getCapacity() - usedCapacity;
        if (req.getCapacity() > remaining) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Sức chứa vượt quá giới hạn! Tầng \"" + floor.getName()
                            + "\" còn " + remaining + " chỗ trống (tối đa: " + floor.getCapacity() + ")");
        }

        ParkingZone zone = ParkingZone.builder()
                .name(req.getName())
                .floor(floor)
                .vehicleType(vehicleType)
                .capacity(req.getCapacity())
                .build();

        return toResponse(zoneRepository.save(zone));
    }

    // Cập nhật thông tin khu vực đỗ xe
    /* Tìm zone theo id — ném lỗi 404 nếu không tồn tại
       Tìm tầng mới theo floorId — ném lỗi 404 nếu không tồn tại
       Lấy loại xe từ tầng mới — ném lỗi 400 nếu tầng chưa có loại xe
       Tính sức chứa đã dùng bởi các zone KHÁC trong cùng tầng (loại trừ zone hiện tại)
       Tính remaining = capacity tầng - sức chứa đã dùng bởi các zone khác
       Nếu sức chứa mới > remaining → ném lỗi 400
       Cập nhật các trường, lưu lại và trả về ZoneResponse */
    public ZoneResponse updateZone(Integer id, ZoneRequest req) {
        ParkingZone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy khu vực với id: " + id));

        Floor floor = floorRepository.findById(req.getFloorId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy tầng với id: " + req.getFloorId()));

        VehicleType vehicleType = floor.getVehicleType();
        if (vehicleType == null) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Tầng này chưa được cấu hình loại xe. Hãy cập nhật thông tin tầng trước!");
        }

        // Tính remaining, loại trừ zone hiện tại
        int usedOther = zoneRepository.findByFloorId(req.getFloorId()).stream()
                .filter(z -> !z.getId().equals(id))
                .mapToInt(ParkingZone::getCapacity).sum();
        int remaining = floor.getCapacity() - usedOther;
        if (req.getCapacity() > remaining) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Sức chứa vượt quá giới hạn! Tầng \"" + floor.getName()
                            + "\" còn " + remaining + " chỗ trống (tối đa: " + floor.getCapacity() + ")");
        }

        zone.setName(req.getName());
        zone.setFloor(floor);
        zone.setVehicleType(vehicleType);
        zone.setCapacity(req.getCapacity());

        return toResponse(zoneRepository.save(zone));
    }

    // Xóa khu vực đỗ xe theo id (cascade: xóa toàn bộ ô đỗ bên trong trước)
    /* Tìm zone theo id — ném lỗi 404 nếu không tồn tại
       Kiểm tra số ô đỗ đang có xe (OCCUPIED) trong zone
       Nếu có xe → ném lỗi 400 (không thể xóa zone khi còn xe đang đỗ)
       Xóa toàn bộ ô đỗ (slot) thuộc zone này trước (cascade)
       Xóa zone khỏi database */
    public void deleteZone(Integer id) {
        ParkingZone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy khu vực với id: " + id));
        // Không cho xóa nếu còn xe đang đỗ
        long occupiedCount = slotRepository.countByZoneIdAndStatus(id, "OCCUPIED");
        if (occupiedCount > 0) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Không thể xóa khu vực \"" + zone.getName()
                            + "\" vì đang có " + occupiedCount + " xe đang đỗ!");
        }
        // Cascade: xóa tất cả ô đỗ trong zone trước, rồi xóa zone
        slotRepository.deleteByZoneId(id);
        zoneRepository.delete(zone);
    }

    // Chuyển đổi entity ParkingZone thành ZoneResponse kèm thống kê slot thực tế
    /* Xác định loại xe: ưu tiên loại xe của zone, nếu không có thì lấy từ tầng
       Đếm tổng slot, slot trống, slot có xe, slot bảo trì theo zoneId
       Xây dựng ZoneResponse với đầy đủ thông tin và trả về */
    private ZoneResponse toResponse(ParkingZone zone) {
        VehicleType vt = zone.getVehicleType() != null ? zone.getVehicleType()
                : (zone.getFloor() != null ? zone.getFloor().getVehicleType() : null);

        Integer zoneId = zone.getId();
        long total       = slotRepository.countByZoneId(zoneId);
        long empty       = slotRepository.countByZoneIdAndStatus(zoneId, "EMPTY");
        long occupied    = slotRepository.countByZoneIdAndStatus(zoneId, "OCCUPIED");
        long maintenance = slotRepository.countByZoneIdAndStatus(zoneId, "MAINTENANCE");

        return ZoneResponse.builder()
                .id(zone.getId())
                .name(zone.getName())
                .capacity(zone.getCapacity())
                .vehicleTypeId(vt != null ? vt.getId() : null)
                .vehicleTypeName(vt != null ? vt.getName() : null)
                .floorId(zone.getFloor() != null ? zone.getFloor().getId() : null)
                .floorName(zone.getFloor() != null ? zone.getFloor().getName() : null)
                .buildingName(zone.getFloor() != null && zone.getFloor().getBuilding() != null
                        ? zone.getFloor().getBuilding().getName() : null)
                .totalSlots(total)
                .emptySlots(empty)
                .occupiedSlots(occupied)
                .maintenanceSlots(maintenance)
                .build();
    }
}
