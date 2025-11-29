package com.forestplus.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseResponse {
    
    private String landName;      // Nombre del terreno
    private Long treeTypeId;      // Id del tipo de árbol comprado
    private Integer quantity;     // Cantidad de árboles
    private BigDecimal totalPrice; // Precio total de la compra
}
