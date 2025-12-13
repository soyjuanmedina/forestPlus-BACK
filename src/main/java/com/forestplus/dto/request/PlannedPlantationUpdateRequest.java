package com.forestplus.dto.request;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlannedPlantationUpdateRequest {

    @Schema(description = "ID del terreno asociado (opcional)")
    private Long landId;

    @Schema(description = "Fecha prevista de la plantación")
    private LocalDate plannedDate;

    @Schema(description = "Fecha efectiva de la plantación (opcional)")
    private LocalDate effectiveDate;

    @Schema(description = "Cantidad mínima de árboles a plantar")
    private Integer minTrees;

    @Schema(description = "Cantidad óptima de árboles a plantar (opcional)")
    private Integer optimalTrees;

    @Schema(description = "Cantidad máxima de árboles a plantar (opcional)")
    private Integer maxTrees;
}
