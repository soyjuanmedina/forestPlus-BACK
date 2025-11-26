package com.forestplus.service;

import org.springframework.stereotype.Service;

import com.forestplus.dto.request.PurchaseRequest;
import com.forestplus.dto.response.PurchaseResponse;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    @Override
    public PurchaseResponse processPurchase(PurchaseRequest req) {

        // ejemplo de lógica inicial (luego la cambias)
        Long generatedPurchaseId = 1L; // simulado
        BigDecimal totalPrice = req.getPricePerUnit().multiply(BigDecimal.valueOf(req.getQuantity()));

        return PurchaseResponse.builder()
                .purchaseId(generatedPurchaseId)
                .result("OK")
                .message("Purchase processed successfully")
                .landId(req.getLandId())
                .treeTypeId(req.getTreeTypeId())
                .quantity(req.getQuantity())
                .totalPrice(totalPrice)
                .assignedTreeIds(Collections.emptyList()) // por ahora vacío
                .emailsSent(false) // aún no implementado
                .createdAt(Instant.now())
                .build();
    }
}
