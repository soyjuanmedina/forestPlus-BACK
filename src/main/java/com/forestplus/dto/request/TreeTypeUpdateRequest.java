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
    private String description;
    private BigDecimal co2Absorption;
    private BigDecimal typicalHeight;
    private Integer lifespanYears;
}
