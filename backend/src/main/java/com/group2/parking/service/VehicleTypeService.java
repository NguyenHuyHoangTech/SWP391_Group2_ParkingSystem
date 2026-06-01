package com.group2.parking.service;

import com.group2.parking.dto.response.VehicleTypeResponse;
import com.group2.parking.entity.VehicleType;
import com.group2.parking.repository.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleTypeService {

    private final VehicleTypeRepository vehicleTypeRepository;

    public List<VehicleTypeResponse> getAllVehicleTypes() {
        return vehicleTypeRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private VehicleTypeResponse toResponse(VehicleType vt) {
        return VehicleTypeResponse.builder()
                .id(vt.getId())
                .name(vt.getName())
                .description(vt.getDescription())
                .build();
    }
}
