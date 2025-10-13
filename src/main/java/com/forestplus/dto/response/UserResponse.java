package com.forestplus.dto.response;
import com.forestplus.model.RolesEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String surname;
    private String secondSurname;
    private String email;
    private String picture; 
    private RolesEnum role;
    private boolean emailVerified;
    private Boolean forcePasswordChange;
    
    // Información de la compañía como DTO
    private CompanyResponse company;
}