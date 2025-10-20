package com.forestplus.dto.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class CompanyCO2YearlyRequest {

	private Long id; 
	private Integer year; 
	private BigDecimal totalEmissions; 
	private BigDecimal totalCompensations;
}
