package com.forestplus.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreeUpdateRequest {

    private String species;
    private BigDecimal co2Absorption;
    private LocalDate plantedAt;
    private Long treeTypeId;
    private Long landId;
    private Long ownerUserId;
    private Long ownerCompanyId;
    private String customName;
}
