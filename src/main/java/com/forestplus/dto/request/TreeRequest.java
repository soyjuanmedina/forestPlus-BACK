package com.forestplus.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreeRequest {

    private String species;
    private BigDecimal co2Absorption;
    private LocalDate plantedAt;
    private Long treeTypeId;
    private Long landId;
    private Long ownerUserId;     // opcional, null si es compañía
    private Long ownerCompanyId;  // opcional, null si es usuario
}
