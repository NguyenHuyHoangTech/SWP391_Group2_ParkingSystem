package com.group2.parking.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffResponse {

    private Integer id;
    private String username;
    private String email;
    private String phone;
    private String role;
    private Integer buildingId;
    private String status;
}
