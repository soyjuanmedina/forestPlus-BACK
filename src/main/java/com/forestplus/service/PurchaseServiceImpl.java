package com.forestplus.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.forestplus.dto.request.PurchaseRequest;
import com.forestplus.dto.response.PurchaseResponse;
import com.forestplus.entity.LandEntity;
import com.forestplus.entity.TreeTypeEntity;
import com.forestplus.entity.UserEntity;
import com.forestplus.integrations.loops.LoopsService;
import com.forestplus.integrations.loops.dto.LoopsEventRequest;
import com.forestplus.repository.LandRepository;
import com.forestplus.repository.TreeTypeRepository;
import com.forestplus.security.CurrentUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {
	
    @Value("${app.frontend.url}")
    private String frontendUrl;

	private final LandRepository landRepository;
	private final TreeTypeRepository treeTypeRepository;
    private final EmailService emailService;
    private final CurrentUserService currentUserService;
    private final LoopsService loopsService;

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
        String link = frontendUrl;
        
        Map<String, Object> buyerEventProperties = new HashMap<>();
        buyerEventProperties.put("buyerName", buyer.getName());
        buyerEventProperties.put("landName", land.getName());
        buyerEventProperties.put("quantity", request.getQuantity());
        buyerEventProperties.put("totalPrice", totalPrice);
        buyerEventProperties.put("treeType", treeType.getName());
        buyerEventProperties.put("link", link);
        
        LoopsEventRequest buyerLoopsEvent = new LoopsEventRequest(
        	buyer.getEmail(),
            "purchase_thanks",
            buyerEventProperties
        );

        loopsService.sendEvent(buyerLoopsEvent);

        // 7️⃣ Variables y email vendedor
        String sellerEmail = "info@forestplusapp.com";
        // Enviar email de confirmación desde Loops
        Map<String, Object> eventProperties = new HashMap<>();
        eventProperties.put("buyerName", buyer.getName());
        eventProperties.put("buyerMail", buyer.getEmail());
        eventProperties.put("landName", land.getName());
        eventProperties.put("quantity", request.getQuantity());
        eventProperties.put("totalPrice", totalPrice);
        eventProperties.put("treeType", treeType.getName());
        
        LoopsEventRequest loopsEvent = new LoopsEventRequest(
        	sellerEmail,
            "purchase_info_to_admin",
            eventProperties
        );

        loopsService.sendEvent(loopsEvent);

        // 8️⃣ Devolver respuesta
        PurchaseResponse response = new PurchaseResponse();
        response.setLandName(land.getName());
        response.setTreeTypeId(request.getTreeTypeId());
        response.setQuantity(request.getQuantity());
        response.setTotalPrice(totalPrice);

        return response;
    }

}
