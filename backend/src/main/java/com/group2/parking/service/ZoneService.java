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
    private final VehicleTypeRepository vehicleTypeRepository;
    private final SlotRepository slotRepository;

    public List<ZoneResponse> getAllZones() {
        return zoneRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ZoneResponse> getZonesByFloor(Integer floorId) {
        return zoneRepository.findByFloorId(floorId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ZoneResponse getZoneById(Integer id) {
        ParkingZone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy khu vực với id: " + id));
        return toResponse(zone);
    }

    public ZoneResponse createZone(ZoneRequest request) {
        Floor floor = floorRepository.findById(request.getFloorId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy tầng với id: " + request.getFloorId()));

        // Lấy loại xe từ tầng (không cần chọn loại xe ở khu vực nữa)
        VehicleType vehicleType = floor.getVehicleType();
        if (vehicleType == null) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Tầng này chưa được cấu hình loại xe. Vui lòng cập nhật thông tin tầng trước!");
        }

        // Kiểm tra capacity không vượt quá tầng
        List<ParkingZone> existingZones = zoneRepository.findByFloorId(request.getFloorId());
        int usedCapacity = existingZones.stream().mapToInt(ParkingZone::getCapacity).sum();
        int remaining = floor.getCapacity() - usedCapacity;
        if (request.getCapacity() > remaining) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Sức chứa vượt quá giới hạn cho phép! Tầng " + floor.getName()
                            + " còn " + remaining + " chỗ trống (sức chứa tối đa: "
                            + floor.getCapacity() + " chỗ)");
        }

        ParkingZone zone = new ParkingZone();
        zone.setName(request.getName());
        zone.setFloor(floor);
        zone.setVehicleType(vehicleType); // Kế thừa từ tầng
        zone.setCapacity(request.getCapacity());

        return toResponse(zoneRepository.save(zone));
    }

    public ZoneResponse updateZone(Integer id, ZoneRequest request) {
        ParkingZone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy khu vực với id: " + id));

        Floor floor = floorRepository.findById(request.getFloorId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy tầng với id: " + request.getFloorId()));

        VehicleType vehicleType = floor.getVehicleType();
        if (vehicleType == null) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Tầng này chưa được cấu hình loại xe. Vui lòng cập nhật thông tin tầng trước!");
        }

        // Kiểm tra capacity không vượt quá tầng (loại trừ zone hiện tại)
        List<ParkingZone> otherZones = zoneRepository.findByFloorId(request.getFloorId())
                .stream().filter(z -> !z.getId().equals(id)).collect(Collectors.toList());
        int usedCapacity = otherZones.stream().mapToInt(ParkingZone::getCapacity).sum();
        int remaining = floor.getCapacity() - usedCapacity;
        if (request.getCapacity() > remaining) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Sức chứa vượt quá giới hạn cho phép! Tầng " + floor.getName()
                            + " còn " + remaining + " chỗ trống (sức chứa tối đa: "
                            + floor.getCapacity() + " chỗ)");
        }

        zone.setName(request.getName());
        zone.setFloor(floor);
        zone.setVehicleType(vehicleType); // Luôn kế thừa từ tầng
        zone.setCapacity(request.getCapacity());

        return toResponse(zoneRepository.save(zone));
    }

    public void deleteZone(Integer id) {
        ParkingZone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy khu vực với id: " + id));

        if (slotRepository.existsByZoneId(id)) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Không thể xóa khu vực này vì đang có ô đỗ xe bên trong. Hãy xóa các ô đỗ trước!");
        }

        zoneRepository.delete(zone);
    }

    private ZoneResponse toResponse(ParkingZone zone) {
        // Lấy loại xe từ zone (đã kế thừa từ tầng khi tạo)
        String vehicleTypeName = null;
        Integer vehicleTypeId = null;
        if (zone.getVehicleType() != null) {
            vehicleTypeName = zone.getVehicleType().getName();
            vehicleTypeId = zone.getVehicleType().getId();
        } else if (zone.getFloor() != null && zone.getFloor().getVehicleType() != null) {
            // Fallback: lấy từ tầng nếu zone chưa có
            vehicleTypeName = zone.getFloor().getVehicleType().getName();
            vehicleTypeId = zone.getFloor().getVehicleType().getId();
        }

        return ZoneResponse.builder()
                .id(zone.getId())
                .name(zone.getName())
                .capacity(zone.getCapacity())
                .vehicleTypeId(vehicleTypeId)
                .vehicleTypeName(vehicleTypeName)
                .floorId(zone.getFloor() != null ? zone.getFloor().getId() : null)
                .floorName(zone.getFloor() != null ? zone.getFloor().getName() : null)
                .buildingName(zone.getFloor() != null && zone.getFloor().getBuilding() != null
                        ? zone.getFloor().getBuilding().getName() : null)
                .build();
    }
}
