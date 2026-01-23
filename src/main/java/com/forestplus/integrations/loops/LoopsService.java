package com.forestplus.integrations.loops;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.forestplus.integrations.loops.dto.LoopsEventRequest;
import com.forestplus.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoopsService {

    @Value("${loops.api.key}")
    private String loopsApiKey;
    
    @Value("${loops.api.base-url}")
    private String loopsApiBaseUrl;
    
    @Value("${loops.api.contacts-url}")
    private String loopsContactsUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    
    private final UserRepository userRepository;
    
    // -------------------------------------------------
    // 1️⃣ Crear / actualizar contacto (Audience)
    // -------------------------------------------------
    public void upsertContact(String email, Map<String, Object> contactProperties) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + loopsApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.putAll(contactProperties);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(loopsContactsUrl, entity, String.class);
            System.out.println("Contacto Loops upserted: " + email);
        } catch (Exception e) {
            System.err.println("Error upsertContact Loops: " + e.getMessage());
        }
    }

    public void sendEvent(LoopsEventRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + loopsApiKey);

        HttpEntity<LoopsEventRequest> entity = new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(loopsApiBaseUrl, entity, String.class);
            System.out.println("Evento enviado a Loops: " + request.getEventName() + " -> " + request.getEmail());
        } catch (Exception e) {
            System.err.println("Error enviando evento a Loops: " + e.getMessage());
        }
    }
    
    public void contactUnsubscribed(String email) {
        userRepository.findByEmail(email)
            .ifPresent(user -> {
                user.setReceiveEmails(false);
                userRepository.save(user);
            });
    }
}
