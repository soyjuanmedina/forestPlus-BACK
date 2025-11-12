package com.forestplus.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TreeTypeResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal co2Absorption;
    private BigDecimal typicalHeight;
    private Integer lifespanYears;
    private String picture;
}
