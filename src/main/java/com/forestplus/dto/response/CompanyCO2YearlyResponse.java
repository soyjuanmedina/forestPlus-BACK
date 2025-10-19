package com.forestplus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCO2YearlyResponse {
	
    private Long id;
    private Integer year;
    private BigDecimal totalEmissions;
    private BigDecimal totalCompensations;

}
