package com.forestplus.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private String species;
    private BigDecimal co2Absorption;
    private LocalDate plantedAt;
    private Long treeTypeId;
    private String treeTypeName;
    private TreeTypeResponse treeType;
    private Long landId;
    private String landName;
    private LandResponse land;
    private Long ownerUserId;
    private String ownerUserName;
    private Long ownerCompanyId;
    private String ownerCompanyName;
    private String customName;
}
