package com.forestplus.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreeUpdateRequest {

    private String scientificName;
    private String customName;

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

    private LocalDate plantedAt;
    private Long treeTypeId;
    private Long landId;
    private Long ownerUserId;
    private Long ownerCompanyId;
    private Long plannedPlantationId;

    private String picture; // URL o path a la foto del árbol
}
