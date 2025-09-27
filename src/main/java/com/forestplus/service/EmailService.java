package com.forestplus.service;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String contentTemplate, Map<String, Object> variables) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            // Renderiza primero el contenido dinámico
            Context context = new Context();
            context.setVariables(variables);
            String contentHtml = templateEngine.process(contentTemplate, context);

            // Ahora lo inserta en la plantilla base
            context.setVariable("title", subject);
            context.setVariable("content", contentHtml);
            String html = templateEngine.process("email-base.html", context);

            helper.setText(html, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("noreply@forestplus.com");
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error enviando correo", e);
        }
    }

    
    public void sendVerificationEmail(String to, String name, String link) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            // Contexto Thymeleaf
            Context contentContext = new Context();
            contentContext.setVariable("name", name);
            contentContext.setVariable("link", link);

            // Renderizamos primero SOLO el contenido
            String contentHtml = templateEngine.process("contents/verify-email-content", contentContext);

            // Ahora contexto para el base
            Context baseContext = new Context();
            baseContext.setVariable("title", "Confirma tu correo en ForestPlus");
            baseContext.setVariable("content", contentHtml);

            // Renderizamos el base.html con el contenido dentro
            String html = templateEngine.process("email-base", baseContext);

            helper.setText(html, true); // true = HTML
            helper.setTo(to);
            helper.setSubject("Confirma tu correo en ForestPlus");
            helper.setFrom("noreply@forestplus.com");
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Error enviando correo", e);
        }
    }
}
