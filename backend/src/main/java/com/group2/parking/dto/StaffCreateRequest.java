package com.group2.parking.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffCreateRequest {

    private String username;
    private String password;
    private String email;
    private String phone;
    private String role;
    private Integer buildingId;
}
