package com.forestplus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyResponse {
    private Long id;
    private String name;
    private String address;
    private String picture; 
    private UserSummaryResponse admin;
    private List<UserSummaryResponse> users;
    private LocalDateTime createdAt;
    
    @ArraySchema(schema = @Schema(implementation = CompanyEmissionResponse.class))
    private List<CompanyEmissionResponse> emissions;
    
    @ArraySchema(schema = @Schema(implementation = CompanyCompensationResponse.class))
    private List<CompanyCompensationResponse> compensations;
}

