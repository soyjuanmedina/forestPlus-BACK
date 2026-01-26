package com.forestplus.integrations.loops;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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

    public boolean isValid(String headerSignature, String payload) {
    	
    	log.info("Expected: v1={}", hmacSha256(payload, signingSecret));
        // header suele venir como: "v1=abcdef..."
        String expected = "v1=" + hmacSha256(payload, signingSecret);
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                headerSignature.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec key =
                    new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(key);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(rawHmac);
        } catch (Exception e) {
            throw new IllegalStateException("Error verifying Loops signature", e);
        }
    }
}