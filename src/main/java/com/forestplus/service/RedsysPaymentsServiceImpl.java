package com.forestplus.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.forestplus.dto.request.RedsysNotificationRequest;
import com.forestplus.dto.response.RedsysPaymentResponse;
import com.forestplus.entity.OrderEntity;
import com.forestplus.repository.OrdersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedsysPaymentsServiceImpl implements RedsysPaymentsService {

    private final OrdersRepository ordersRepository;

    // Valores de Redsys (en producción, mejor en application.properties o env)
    @Value("${redsys.merchant-code}")
    private String merchantCode;

    @Value("${redsys.terminal}")
    private String terminal;

    @Value("${redsys.secret-key}")
    private String secretKey;

    @Value("${redsys.url}")
    private String redsysUrl;

    @Value("${redsys.url-ok}")
    private String urlOk;

    @Value("${redsys.url-ko}")
    private String urlKo;

    @Override
    @Transactional(readOnly = true)
    public RedsysPaymentResponse generateRedsysPayment(Long orderId) {
        // 1️⃣ Obtener pedido
        OrderEntity order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // 2️⃣ Calcular importe en céntimos
        long amount = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValue();

        // 3️⃣ Número de pedido para Redsys
        String redsysOrder = String.format("%012d", order.getId());

        // 4️⃣ Generar firma (simplificada, en producción usar algoritmo oficial)
        String signature;
        try {
            String message = merchantCode + redsysOrder + amount;
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            signature = Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error generando firma Redsys", e);
        }

        // 5️⃣ Preparar parámetros
        Map<String, String> parameters = new HashMap<>();
        parameters.put("Ds_Merchant_Amount", String.valueOf(amount));
        parameters.put("Ds_Merchant_Order", redsysOrder);
        parameters.put("Ds_Merchant_MerchantCode", merchantCode);
        parameters.put("Ds_Merchant_Terminal", terminal);
        parameters.put("Ds_Merchant_MerchantSignature", signature);
        parameters.put("Ds_Merchant_UrlOK", urlOk);
        parameters.put("Ds_Merchant_UrlKO", urlKo);

        return RedsysPaymentResponse.builder()
                .redsysUrl(redsysUrl)
                .parameters(parameters)
                .build();
    }

    @Override
    @Transactional
    public void handleRedsysNotification(RedsysNotificationRequest notification) {
        // 1️⃣ Obtener pedido
        Long orderId = Long.parseLong(notification.getDs_Order());
        OrderEntity order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // 2️⃣ Validar firma (simplificada)
        long amount = order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValue();
        String expectedSignature = Base64.getEncoder().encodeToString(
                (merchantCode + notification.getDs_Order() + amount).getBytes(StandardCharsets.UTF_8)
        );

        if (!expectedSignature.equals(notification.getDs_Signature())) {
            throw new RuntimeException("Firma inválida");
        }

        // 3️⃣ Actualizar estado del pedido
        if ("0000".equals(notification.getDs_Response())) {
            order.setStatus("paid");
        } else {
            order.setStatus("failed");
        }

        ordersRepository.save(order);
    }
}
