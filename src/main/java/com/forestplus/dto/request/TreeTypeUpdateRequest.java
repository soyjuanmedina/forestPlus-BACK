package com.forestplus.dto.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreeTypeUpdateRequest {

    private String name;
    private String scientificName;
    private String description;

    private BigDecimal co2AbsorptionAt20;
    private BigDecimal co2AbsorptionAt25;
    private BigDecimal co2AbsorptionAt30;
    private BigDecimal co2AbsorptionAt35;
    private BigDecimal co2AbsorptionAt40;

    private BigDecimal typicalHeight;
    private Integer lifespanYears;
}
