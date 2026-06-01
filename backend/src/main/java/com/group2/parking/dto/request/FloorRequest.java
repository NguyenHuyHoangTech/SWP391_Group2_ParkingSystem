package com.group2.parking.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FloorRequest {

    @NotBlank(message = "Tên tầng không được để trống")
    private String name;

    @NotNull(message = "Cấp tầng không được để trống")
    private Integer floorLevel;

    @NotNull(message = "Sức chứa không được để trống")
    @Min(value = 1, message = "Sức chứa phải lớn hơn 0")
    private Integer capacity;

    @NotNull(message = "Tòa nhà không được để trống")
    private Integer buildingId;

    @NotNull(message = "Loại xe không được để trống")
    private Integer vehicleTypeId;
}
