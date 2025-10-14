package com.forestplus.dto.response;
import com.forestplus.model.RolesEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    private String surname;
    private String secondSurname;
    private String picture; 
    private RolesEnum role;
    private boolean emailVerified;
    private Boolean forcePasswordChange;
    
    private CompanyResponse company;
}
