package com.forestplus.dto.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HomeDashboardKpiResponse {

    /**
     * Número total de árboles plantados por el usuario y sus empresas
     */
    private long plantedTrees;
    
    /**
     * Número total de árboles pendientes de plantar por el usuario y sus empresas
     */
    private long pendingTreesCount;
    
    private BigDecimal annualCo2Compensated;
    
    private List<PlannedPlantationKpiResponse> plannedPlantations;

}