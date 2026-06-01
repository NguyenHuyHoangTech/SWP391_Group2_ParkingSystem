package com.group2.parking.service;

import com.group2.parking.dto.response.BuildingResponse;
import com.group2.parking.repository.ParkingBuildingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final ParkingBuildingRepository buildingRepository;

    public List<BuildingResponse> getAllBuildings() {
        return buildingRepository.findAll().stream()
                .map(b -> BuildingResponse.builder()
                        .id(b.getId())
                        .name(b.getName())
                        .address(b.getAddress())
                        .status(b.getStatus())
                        .build())
                .collect(Collectors.toList());
    }
}
