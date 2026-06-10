/* Lớp Service chứa toàn bộ logic nghiệp vụ quản lý tầng (Floor).
   Xử lý các nghiệp vụ: tạo, cập nhật, xóa tầng kèm kiểm tra sức chứa,
   và tổng hợp thống kê số lượng ô đỗ theo trạng thái cho mỗi tầng. */

package com.group2.parking.service;

import com.group2.parking.dto.request.FloorRequest;
import com.group2.parking.dto.response.FloorResponse;
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
public class FloorService {

    private final FloorRepository floorRepository;
    private final ParkingBuildingRepository buildingRepository;
    private final ParkingZoneRepository zoneRepository;
    private final VehicleTypeJpaRepository vehicleTypeRepository;
    private final SlotRepository slotRepository;

    // Lấy toàn bộ danh sách tầng kèm thống kê
    /* Truy vấn tất cả tầng trong database
       Chuyển đổi từng Floor thành FloorResponse (kèm thống kê slot)
       Trả về danh sách kết quả */
    public List<FloorResponse> getAllFloors() {
        return floorRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Lấy thông tin một tầng theo id
    /* Tìm tầng theo id trong database
       Nếu không tồn tại → ném ngoại lệ 404 Not Found
       Chuyển đổi thành FloorResponse và trả về */
    public FloorResponse getFloorById(Integer id) {
        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy tầng với id: " + id));
        return toResponse(floor);
    }

    // Tạo mới một tầng
    /* Tìm tòa nhà theo buildingId — ném lỗi 404 nếu không có
       Tìm loại xe theo vehicleTypeId — ném lỗi 404 nếu không có
       Tạo đối tượng Floor mới với các thông tin từ request
       Lưu vào database và trả về FloorResponse */
    public FloorResponse createFloor(FloorRequest req) {
        ParkingBuilding building = buildingRepository.findById(req.getBuildingId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy tòa nhà với id: " + req.getBuildingId()));
        VehicleType vehicleType = vehicleTypeRepository.findById(req.getVehicleTypeId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy loại xe với id: " + req.getVehicleTypeId()));

        Floor floor = Floor.builder()
                .name(req.getName())
                .floorLevel(req.getFloorLevel())
                .capacity(req.getCapacity())
                .building(building)
                .vehicleType(vehicleType)
                .build();

        return toResponse(floorRepository.save(floor));
    }

    // Cập nhật thông tin một tầng
    /* Tìm tầng theo id — ném lỗi 404 nếu không tồn tại
       Tìm tòa nhà và loại xe mới — ném lỗi 404 nếu không tồn tại
       Tính tổng sức chứa đã dùng bởi các zone hiện tại trên tầng này
       Nếu sức chứa mới < sức chứa đã dùng → ném lỗi 400 (không thể thu hẹp)
       Cập nhật các trường và lưu lại
       Trả về FloorResponse đã cập nhật */
    public FloorResponse updateFloor(Integer id, FloorRequest req) {
        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy tầng với id: " + id));

        ParkingBuilding building = buildingRepository.findById(req.getBuildingId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy tòa nhà với id: " + req.getBuildingId()));
        VehicleType vehicleType = vehicleTypeRepository.findById(req.getVehicleTypeId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy loại xe với id: " + req.getVehicleTypeId()));

        int usedCapacity = zoneRepository.findByFloorId(id).stream()
                .mapToInt(ParkingZone::getCapacity).sum();
        if (req.getCapacity() < usedCapacity) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Không thể giảm sức chứa xuống " + req.getCapacity()
                            + " vì các khu vực đang dùng tổng cộng " + usedCapacity + " chỗ!");
        }

        floor.setName(req.getName());
        floor.setFloorLevel(req.getFloorLevel());
        floor.setCapacity(req.getCapacity());
        floor.setBuilding(building);
        floor.setVehicleType(vehicleType);

        return toResponse(floorRepository.save(floor));
    }

    // Xóa một tầng theo id
    /* Tìm tầng theo id — ném lỗi 404 nếu không tồn tại
       Kiểm tra xem tầng còn khu vực (zone) nào không
       Nếu còn zone → ném lỗi 400 yêu cầu xóa zone trước
       Nếu không còn zone → xóa tầng khỏi database */
    public void deleteFloor(Integer id) {
        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy tầng với id: " + id));
        if (zoneRepository.existsByFloorId(id)) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Không thể xóa tầng này vì đang có khu vực đỗ xe. Hãy xóa các khu vực trước!");
        }
        floorRepository.delete(floor);
    }

    // Chuyển đổi entity Floor thành FloorResponse kèm thống kê slot thực tế
    /* Tính tổng sức chứa đã phân bổ cho các zone trong tầng (usedCapacity)
       Đếm tổng slot, slot trống, slot có xe, slot bảo trì theo floorId
       Xây dựng FloorResponse với đầy đủ thông tin và trả về */
    private FloorResponse toResponse(Floor floor) {
        int usedCapacity = zoneRepository.findByFloorId(floor.getId()).stream()
                .mapToInt(ParkingZone::getCapacity).sum();

        Integer floorId = floor.getId();
        long totalSlots    = slotRepository.countByFloorId(floorId);
        long emptySlots    = slotRepository.countByFloorIdAndStatus(floorId, "EMPTY");
        long occupiedSlots = slotRepository.countByFloorIdAndStatus(floorId, "OCCUPIED");
        long maintSlots    = slotRepository.countByFloorIdAndStatus(floorId, "MAINTENANCE");

        return FloorResponse.builder()
                .id(floor.getId())
                .name(floor.getName())
                .floorLevel(floor.getFloorLevel())
                .capacity(floor.getCapacity())
                .buildingId(floor.getBuilding() != null ? floor.getBuilding().getId() : null)
                .buildingName(floor.getBuilding() != null ? floor.getBuilding().getName() : null)
                .vehicleTypeId(floor.getVehicleType() != null ? floor.getVehicleType().getId() : null)
                .vehicleTypeName(floor.getVehicleType() != null ? floor.getVehicleType().getName() : null)
                .usedCapacity(usedCapacity)
                .remainingCapacity(floor.getCapacity() - usedCapacity)
                .totalSlots(totalSlots)
                .emptySlots(emptySlots)
                .occupiedSlots(occupiedSlots)
                .maintenanceSlots(maintSlots)
                .build();
    }
}
