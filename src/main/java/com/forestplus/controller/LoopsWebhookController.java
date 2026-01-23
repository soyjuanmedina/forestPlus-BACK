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
            @RequestHeader("X-Loops-Signature") String signature,
            @RequestBody String rawBody
    ) {

        if (!loopsSignatureVerifier.isValid(signature, rawBody)) {
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