package com.forestplus.dto.response;

import com.forestplus.dto.response.CompanyResponse;
import com.forestplus.dto.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LandResponse {
    private Long id;
    private String name;
    private String location;
    private BigDecimal area;
    private String picture;

    // Lista de usuarios que tienen acceso a la parcela
    private List<UserResponse> users;

    // Lista de compañías que poseen o están asociadas a la parcela
    private List<CompanyResponse> companies;

    private LocalDateTime createdAt;
}
