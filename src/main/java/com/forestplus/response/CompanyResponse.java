package com.forestplus.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyResponse {
    private Long id;
    private String name;
    private String address;
    private UserResponse admin;          // Admin de la compañía
    private List<UserResponse> users;    // Usuarios de la compañía
    private LocalDateTime createdAt;
}
