package com.forestplus.dto.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private String scientificName;
    private String description;

    @Schema(name = "co2AbsorptionAt20")
    private BigDecimal co2AbsorptionAt20;

    @Schema(name = "co2AbsorptionAt25")
    private BigDecimal co2AbsorptionAt25;

    @Schema(name = "co2AbsorptionAt30")
    private BigDecimal co2AbsorptionAt30;

    @Schema(name = "co2AbsorptionAt35")
    private BigDecimal co2AbsorptionAt35;

    @Schema(name = "co2AbsorptionAt40")
    private BigDecimal co2AbsorptionAt40;

    private BigDecimal typicalHeight;
    private Integer lifespanYears;
    private String picture;
}
