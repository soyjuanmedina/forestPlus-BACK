package com.forestplus.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private JavaMailSender mailSender;
    private TemplateEngine templateEngine;
    private EmailService emailService;
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        templateEngine = mock(TemplateEngine.class);
        emailService = new EmailService(mailSender, templateEngine);

        mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>TEST</html>");
    }

    @Test
    void shouldSendEmail() {
        emailService.sendEmail("user@test.com", "Hola", "template", Map.of("var", "value"));

        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void shouldSendVerificationEmail() {
        emailService.sendVerificationEmail("user@test.com", "Juan", "http://link.com");

        verify(mailSender, times(1)).send(mimeMessage);
    }
}