package com.forestplus.dto.request;

import com.forestplus.model.RolesEnum;

import lombok.Data;

@Data
public class RegisterUserByAdminRequest {
    private String name;
    private String surname;
    private String secondSurname;
    private String email;
    private RolesEnum role;
    private Long companyId;
}
