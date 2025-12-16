package com.forestplus.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.forestplus.dto.request.PurchaseRequest;
import com.forestplus.dto.response.PurchaseResponse;
import com.forestplus.entity.LandEntity;
import com.forestplus.entity.TreeTypeEntity;
import com.forestplus.entity.UserEntity;
import com.forestplus.repository.LandRepository;
import com.forestplus.repository.TreeTypeRepository;
import com.forestplus.security.CurrentUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

	private final LandRepository landRepository;
	private final TreeTypeRepository treeTypeRepository;
    private final EmailService emailService;
    private final CurrentUserService currentUserService;

    @Transactional
    @Override
    public PurchaseResponse processPurchase(PurchaseRequest request) {

        // 1️⃣ Obtener terreno
        LandEntity land = landRepository.findById(request.getLandId())
                .orElseThrow(() -> new RuntimeException("Terreno no encontrado"));

        // 2️⃣ Obtener tipo de árbol
        TreeTypeEntity treeType = treeTypeRepository.findById(request.getTreeTypeId())
                .orElseThrow(() -> new RuntimeException("Tipo de árbol no encontrado"));

        // 3️⃣ Obtener comprador
        UserEntity buyer = currentUserService.getCurrentUser();
        if (buyer == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        // 4️⃣ Actualizar árboles pendientes
        buyer.setPendingTreesCount(buyer.getPendingTreesCount() + request.getQuantity());

        // 5️⃣ Calcular total
        BigDecimal totalPrice = request.getPricePerUnit().multiply(BigDecimal.valueOf(request.getQuantity()));

        // 6️⃣ Variables y email comprador
        Map<String, Object> buyerVars = new HashMap<>();
        buyerVars.put("buyerName", buyer.getName());
        buyerVars.put("landName", land.getName());
        buyerVars.put("quantity", request.getQuantity());
        buyerVars.put("totalPrice", totalPrice);
        buyerVars.put("treeType", treeType.getName());

        emailService.sendEmail(
                buyer.getEmail(),
                "Compra realizada - Forest+",
                "contents/purchase-buyer-content",
                buyerVars
        );

        // 7️⃣ Variables y email vendedor
        Map<String, Object> sellerVars = new HashMap<>();
        sellerVars.put("sellerName", "Administrador");
        sellerVars.put("buyerName", buyer.getName());
        sellerVars.put("buyerMail", buyer.getEmail());
        sellerVars.put("landName", land.getName());
        sellerVars.put("quantity", request.getQuantity());
        sellerVars.put("totalPrice", totalPrice);
        sellerVars.put("treeType", treeType.getName());

        String sellerEmail = "info@forestplusapp.com";
        emailService.sendEmail(
                sellerEmail,
                "Tu terreno ha recibido una compra - Forest+",
                "contents/purchase-seller-content",
                sellerVars
        );

        // 8️⃣ Devolver respuesta
        PurchaseResponse response = new PurchaseResponse();
        response.setLandName(land.getName());
        response.setTreeTypeId(request.getTreeTypeId());
        response.setQuantity(request.getQuantity());
        response.setTotalPrice(totalPrice);

        return response;
    }

}
