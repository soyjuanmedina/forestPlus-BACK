package com.forestplus.dto.request;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrderRequest {
    private Long userId;      // Si es compra de usuario
    private Long companyId;   // Si es compra de empresa
    private BigDecimal totalAmount;
}
