package com.forestplus.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlannedPlantationKpiResponse {
    private Long plantationId;
    private String plantationName; // opcional, si quieres mostrar nombre o terreno
    private int minTrees;
    private long soldTrees;
}
