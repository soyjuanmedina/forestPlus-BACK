package com.forestplus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forestplus.integrations.loops.LoopsService;
import com.forestplus.integrations.loops.LoopsSignatureVerifier;
import com.forestplus.integrations.loops.dto.LoopsWebhookEvent;
import com.forestplus.service.AuthService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/api/webhooks/loops", produces = "application/json")
@RequiredArgsConstructor
@Slf4j
public class LoopsWebhookController {
	
    private final LoopsService loopsService;
    private final LoopsSignatureVerifier loopsSignatureVerifier;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<Void> handleWebhook(
    		@RequestHeader("Webhook-Signature") String signature,
    		@RequestHeader("Webhook-Id") String webhookId,
    		@RequestHeader("Webhook-Timestamp") String webhookTimestamp,
            @RequestBody String rawBody
    ) {
    	
    	String signedContent = webhookId + "." + webhookTimestamp + "." + rawBody;
    	
        // Logs para depuración
        log.info("📨 Header Webhook-Signature: {}", signature);
        log.info("🆔 Webhook-Id: {}", webhookId);
        log.info("⏱ Webhook-Timestamp: {}", webhookTimestamp);
        log.info("🖋 Raw body length: {}, first 200 chars: {}", rawBody.length(),
                 rawBody.substring(0, Math.min(rawBody.length(), 200)));
        log.info("🔐 Signed content used for HMAC (first 200 chars): {}", 
                 signedContent.substring(0, Math.min(signedContent.length(), 200)));

        if (!loopsSignatureVerifier.isValid(signature, signedContent)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            LoopsWebhookEvent event =
                    objectMapper.readValue(rawBody, LoopsWebhookEvent.class);

            if (event.isContactUnsubscribed()) {
                event.getEmailAddress().ifPresent(email ->
                    loopsService.contactUnsubscribed(email)
                );
            }

        } catch (JsonProcessingException e) {
            // ❌ payload inválido o esquema cambiado
            log.error("Invalid Loops webhook payload", e);
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }
}