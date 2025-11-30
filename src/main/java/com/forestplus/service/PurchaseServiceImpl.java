package com.forestplus.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

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

    @Override
    public PurchaseResponse processPurchase(PurchaseRequest request) {

        // 1️⃣ Obtener terreno
        LandEntity land = landRepository.findById(request.getLandId())
                .orElseThrow(() -> new RuntimeException("Terreno no encontrado"));
        
        // 1️⃣ Obtener terreno
        TreeTypeEntity treeType = treeTypeRepository.findById(request.getTreeTypeId())
                .orElseThrow(() -> new RuntimeException("Tipo de árbol no encontrado"));

        // 2️⃣ Obtener comprador
        UserEntity buyer = currentUserService.getCurrentUser();
        if (buyer == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        // 3️⃣ Calcular total
        BigDecimal totalPrice = request.getPricePerUnit().multiply(BigDecimal.valueOf(request.getQuantity()));

        // 4️⃣ Variables para el template de comprador
        Map<String, Object> buyerVars = new HashMap<>();
        buyerVars.put("buyerName", buyer.getName());
        buyerVars.put("landName", land.getName());
        buyerVars.put("quantity", request.getQuantity());
        buyerVars.put("totalPrice", totalPrice);
        buyerVars.put("treeType", treeType.getName());

        // 5️⃣ Enviar email al comprador
        emailService.sendEmail(
                buyer.getEmail(),
                "Compra realizada - Forest+",
                "contents/purchase-buyer-content",
                buyerVars
        );

        // 6️⃣ Variables para el vendedor
        Map<String, Object> sellerVars = new HashMap<>();
        sellerVars.put("sellerName", "Administrador");
        sellerVars.put("landName", land.getName());
        sellerVars.put("quantity", request.getQuantity());
        sellerVars.put("totalPrice", totalPrice);
        sellerVars.put("treeType", treeType.getName());

        // 7️⃣ Enviar email al vendedor (ya conocido)
        String sellerEmail = "soyjuanmedina@gmail.com"; // aquí tu mail real
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
