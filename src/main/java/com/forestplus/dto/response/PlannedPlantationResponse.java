package com.forestplus.dto.response;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlannedPlantationResponse {

    private Long id;

    // Parcela / terreno (opcional)
    private Long landId;
    private String landName;
    private LandResponse land;

    // Fechas
    @Schema(description = "Fecha prevista de la plantación")
    private LocalDate plannedDate;

    @Schema(description = "Fecha efectiva de la plantación (opcional)")
    private LocalDate effectiveDate;

    // Cantidades de árboles
    @Schema(description = "Cantidad mínima de árboles a plantar")
    private Integer minTrees;

    @Schema(description = "Cantidad óptima de árboles a plantar (opcional)")
    private Integer optimalTrees;

    @Schema(description = "Cantidad máxima de árboles a plantar (opcional)")
    private Integer maxTrees;
    
    private Boolean isActive;
}
