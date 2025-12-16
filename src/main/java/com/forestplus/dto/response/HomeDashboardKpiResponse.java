package com.forestplus.dto.response;

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
    
    private List<PlannedPlantationKpiResponse> plannedPlantations;

}