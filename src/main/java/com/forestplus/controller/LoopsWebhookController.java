package com.forestplus.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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

import jakarta.servlet.http.HttpServletRequest;
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
            HttpServletRequest request
    ) throws IOException {

        byte[] rawBytes = request.getInputStream().readAllBytes();
        String rawBody = new String(rawBytes, StandardCharsets.UTF_8);

        if (!loopsSignatureVerifier.isValid(signature, webhookId, webhookTimestamp, rawBody)) {
            log.warn("Invalid Loops webhook signature!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        LoopsWebhookEvent event =
                objectMapper.readValue(rawBody, LoopsWebhookEvent.class);

        if (event.isEmailUnsubscribed()) {
            event.getEmailAddress().ifPresent(email ->
                loopsService.contactUnsubscribed(email)
            );
        }
        
        if (event.isEmailResubscribed()) {
            event.getEmailAddress().ifPresent(email ->
                loopsService.contactResubscribed(email)
            );
        }
        
        return ResponseEntity.ok().build();
    }
}