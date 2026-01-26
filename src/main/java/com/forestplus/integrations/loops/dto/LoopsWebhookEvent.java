package com.forestplus.integrations.loops.dto;

import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoopsWebhookEvent {
	private String eventName;
    private Long eventTime;
    private String webhookSchemaVersion;
    private ContactIdentity contactIdentity;
    private EmailPayload email; // opcional para email.* events

    // getters y setters

    public boolean isEmailUnsubscribed() {
        return "email.unsubscribed".equals(eventName);
    }
    
    public boolean isEmailResubscribed() {
        return "email.resubscribed".equals(eventName);
    }

    public Optional<String> getEmailAddress() {
        if (contactIdentity != null) {
            return Optional.ofNullable(contactIdentity.getEmail());
        }
        return Optional.empty();
    }
}
