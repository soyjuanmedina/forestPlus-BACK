package com.forestplus.dto.response;

import java.math.BigDecimal;
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
public class TreeResponse {

    private Long id;

    // Nombre científico y nombre personalizado
    private String scientificName;
    private String customName;

    // Valores de CO₂
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

    // Foto del árbol
    private String picture;

    // Fechas
    private LocalDate plantedAt;

    // Tipo de árbol
    private Long treeTypeId;
    private String treeTypeName;
    private TreeTypeResponse treeType;

    // Parcela / terreno
    private Long landId;
    private String landName;
    private LandResponse land;

    // Propietarios
    private Long ownerUserId;
    private String ownerUserName;
    private Long ownerCompanyId;
    private String ownerCompanyName;
}
