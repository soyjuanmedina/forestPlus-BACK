package com.forestplus.request;

import lombok.Data;

@Data
public class CompanyRequest {
    private String name;
    private String address;
    private Long adminId; // Enviar solo el ID del usuario administrador
}
