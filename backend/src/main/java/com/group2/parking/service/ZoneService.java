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

    public List<ZoneResponse> getAllZones() {
        return zoneRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ZoneResponse> getZonesByFloor(Integer floorId) {
        return zoneRepository.findByFloorId(floorId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ZoneResponse getZoneById(Integer id) {
        return toResponse(zoneRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy khu vực với id: " + id)));
    }

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
        VehicleType vt = zone.getVehicleType() != null ? zone.getVehicleType()
                : (zone.getFloor() != null ? zone.getFloor().getVehicleType() : null);
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
                .build();
    }
}
