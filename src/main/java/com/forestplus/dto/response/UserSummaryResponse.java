package com.forestplus.dto.response;

import com.forestplus.model.RolesEnum;

import lombok.Data;

@Data
public class UserSummaryResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private RolesEnum role;
}