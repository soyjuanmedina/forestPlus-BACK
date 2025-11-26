package com.forestplus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseResponse {
    private Long purchaseId;                // id interno de la compra / orden
    private String result;                  // "OK" | "KO"
    private String message;                 // texto explicativo
    private Long landId;                    // land seleccionado
    private Long treeTypeId;                // tipo de árbol
    private Integer quantity;               // cantidad solicitada
    private BigDecimal totalPrice;          // precio calculado (si aplica)
    private List<Long> assignedTreeIds;     // ids de árboles asignados (si aplica)
    private Boolean emailsSent;             // si el back envió notificaciones por correo
    private Instant createdAt;              // timestamp de la operación
}
