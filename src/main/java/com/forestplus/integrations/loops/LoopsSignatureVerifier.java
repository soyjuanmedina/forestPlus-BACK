package com.forestplus.integrations.loops;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HexFormat;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.forestplus.controller.LoopsWebhookController;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoopsSignatureVerifier {

    @Value("${loops.signing.secret}")
    private String signingSecret;

    public boolean isValid(String headerSignature, String webhookId, String webhookTimestamp, String rawBody) {

        try {
            if (headerSignature == null || webhookId == null || webhookTimestamp == null || rawBody == null) {
                log.warn("Missing required values for webhook verification");
                return false;
            }

            // Quitar prefijo "v1," si lo tiene
            if (headerSignature.startsWith("v1,")) {
                headerSignature = headerSignature.substring(3);
            }

            // Construir el contenido firmado
            String signedContent = webhookId + "." + webhookTimestamp + "." + rawBody;

            // Log para depuración
            log.info("Signed content for HMAC: {}", signedContent);
            log.info("Header signature: {}", headerSignature);

            // Extraer la parte Base64 del secret después del guion bajo
            String secretPart = signingSecret.split("_")[1];
            byte[] secretBytes = Base64.getDecoder().decode(secretPart);

            // Crear HMAC SHA256
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(secretBytes, "HmacSHA256");
            mac.init(key);

            byte[] rawHmac = mac.doFinal(signedContent.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = Base64.getEncoder().encodeToString(rawHmac);

            log.info("Expected signature: {}", expectedSignature);

            return expectedSignature.equals(headerSignature);

        } catch (Exception e) {
            log.error("Error verifying Loops signature", e);
            return false;
        }
    }
}