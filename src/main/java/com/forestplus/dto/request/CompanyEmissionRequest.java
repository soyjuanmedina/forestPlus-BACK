package com.forestplus.dto.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyEmissionRequest {
    private Long companyId;
    private Integer year;
    private BigDecimal totalEmissions;
}
