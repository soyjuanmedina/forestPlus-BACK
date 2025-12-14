package com.forestplus.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreeBatchPlantRequest {
    private Long landId;
    private Long treeTypeId;
    private int quantity;
    private Long ownerUserId;
    private Long ownerCompanyId;
    // 🔹 Nueva propiedad para asignar planned plantation
    private Long plannedPlantationId; // opcional
}
