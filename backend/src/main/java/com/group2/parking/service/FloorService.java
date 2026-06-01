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
    private final VehicleTypeRepository vehicleTypeRepository;

    public List<FloorResponse> getAllFloors() {
        return floorRepository.findAll().stream()
                .map(this::toResponseWithCapacity)
                .collect(Collectors.toList());
    }

    public List<FloorResponse> getFloorsByBuilding(Integer buildingId) {
        return floorRepository.findByBuildingId(buildingId).stream()
                .map(this::toResponseWithCapacity)
                .collect(Collectors.toList());
    }

    public FloorResponse getFloorById(Integer id) {
        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy tầng với id: " + id));
        return toResponseWithCapacity(floor);
    }

    public FloorResponse createFloor(FloorRequest request) {
        ParkingBuilding building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy tòa nhà với id: " + request.getBuildingId()));
        VehicleType vehicleType = vehicleTypeRepository.findById(request.getVehicleTypeId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy loại xe với id: " + request.getVehicleTypeId()));

        Floor floor = new Floor();
        floor.setName(request.getName());
        floor.setFloorLevel(request.getFloorLevel());
        floor.setCapacity(request.getCapacity());
        floor.setBuilding(building);
        floor.setVehicleType(vehicleType);

        return toResponseWithCapacity(floorRepository.save(floor));
    }

    public FloorResponse updateFloor(Integer id, FloorRequest request) {
        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy tầng với id: " + id));

        ParkingBuilding building = buildingRepository.findById(request.getBuildingId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy tòa nhà với id: " + request.getBuildingId()));
        VehicleType vehicleType = vehicleTypeRepository.findById(request.getVehicleTypeId())
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,
                        "Không tìm thấy loại xe với id: " + request.getVehicleTypeId()));

        // Kiểm tra nếu giảm capacity xuống dưới lượng đang dùng
        int usedCapacity = zoneRepository.findByFloorId(id).stream()
                .mapToInt(z -> z.getCapacity()).sum();
        if (request.getCapacity() < usedCapacity) {
            throw new AppException(HttpStatus.BAD_REQUEST,
                    "Không thể giảm sức chứa xuống " + request.getCapacity()
                            + " vì các khu vực hiện đang sử dụng tổng cộng " + usedCapacity + " chỗ đỗ!");
        }

        floor.setName(request.getName());
        floor.setFloorLevel(request.getFloorLevel());
        floor.setCapacity(request.getCapacity());
        floor.setBuilding(building);
        floor.setVehicleType(vehicleType);

        return toResponseWithCapacity(floorRepository.save(floor));
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

    private FloorResponse toResponseWithCapacity(Floor floor) {
        int usedCapacity = zoneRepository.findByFloorId(floor.getId()).stream()
                .mapToInt(z -> z.getCapacity()).sum();
        int remainingCapacity = floor.getCapacity() - usedCapacity;

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
                .remainingCapacity(remainingCapacity)
                .build();
    }
}
