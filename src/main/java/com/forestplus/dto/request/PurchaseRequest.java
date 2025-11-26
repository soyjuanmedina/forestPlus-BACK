package com.forestplus.dto.request;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequest {
    private Long landId;
    private Long treeTypeId;
    private Integer quantity;
    private BigDecimal pricePerUnit;
}
