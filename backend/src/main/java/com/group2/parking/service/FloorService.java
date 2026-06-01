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

    public List<FloorResponse> getAllFloors() {
        return floorRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public FloorResponse getFloorById(Integer id) {
        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy tầng với id: " + id));
        return toResponse(floor);
    }

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

    private FloorResponse toResponse(Floor floor) {
        int usedCapacity = zoneRepository.findByFloorId(floor.getId()).stream()
                .mapToInt(ParkingZone::getCapacity).sum();
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
                .build();
    }
}
