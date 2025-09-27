package com.forestplus.service;

import com.forestplus.entity.UserEntity;
import com.forestplus.exception.EmailAlreadyExistsException;
import com.forestplus.exception.EmailAlreadyVerifiedException;
import com.forestplus.exception.EmailNotVerifiedException;
import com.forestplus.exception.ForestPlusException;
import com.forestplus.exception.UserNotFoundException;
import com.forestplus.exception.UuidNotFoundException;
import com.forestplus.exception.WrongPasswordException;
import com.forestplus.mapper.UserMapper;
import com.forestplus.repository.UserRepository;
import com.forestplus.request.RegisterUserRequest;
import com.forestplus.request.ResetPasswordRequest;
import com.forestplus.response.AuthResponse;
import com.forestplus.response.UserResponse;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    
    @Value("${app.frontend.url}")
    private String frontendUrl;

    public UserResponse register(RegisterUserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String verificationUuid = UUID.randomUUID().toString();

        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .secondSurname(request.getSecondSurname())
                .email(request.getEmail())
                .passwordHash(encodedPassword)
                .role(request.getRole())
                .emailVerified(false)
                .forcePasswordChange(false)
                .uuid(verificationUuid)
                .build();

        UserEntity saved = userRepository.save(user);

        // Enviar email de confirmación
        String subject = "Confirma tu correo en ForestPlus";
        String link = frontendUrl + "/verify-email?uuid=" + verificationUuid;
        emailService.sendVerificationEmail(user.getEmail(), user.getName(), link);

        return userMapper.toResponse(saved);
    }

    public AuthResponse login(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new WrongPasswordException();
        }
        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new EmailNotVerifiedException(email);
        }
        String token = jwtService.generateToken(user);
        UserResponse userResponse = userMapper.toResponse(user);
        Boolean forcePasswordChange = user.getForcePasswordChange() ? true : null;
        return new AuthResponse(token, userResponse, forcePasswordChange);
    }
    
    public void verifyEmail(String uuid) {
        UserEntity user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new UuidNotFoundException(uuid)); // nueva excepción

        user.setEmailVerified(true);
        // opcional: limpiar uuid tras verificación
        // user.setUuid(null);
        userRepository.save(user);
    }
    
    public void resendVerificationEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("USER_NOT_FOUND"));

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new EmailAlreadyVerifiedException("EMAIL_ALREADY_VERIFIED");
        }

        // Generar un nuevo UUID si quieres invalidar el anterior
        String verificationUuid = UUID.randomUUID().toString();
        user.setUuid(verificationUuid);
        userRepository.save(user);

        // Enviar email
        String subject = "Confirma tu correo en ForestPlus";
        String link = frontendUrl + "/verify-email?uuid=" + verificationUuid;

        // Variables que usará verify-email-content.html
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", user.getName());
        vars.put("link", link);

        // Llamada al servicio genérico
        emailService.sendEmail(
            user.getEmail(),
            subject,
            "contents/verify-email-content", 
            vars
        );
    }
    
    @Transactional
    public void resetPassword(String email, ResetPasswordRequest request) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        // Guardar nueva contraseña
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        // Marcar forcePasswordChange como false
        user.setForcePasswordChange(false);

        userRepository.save(user);
    }
    
    public void forgotPassword(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        // Generar uuid temporal
        String uuid = UUID.randomUUID().toString();
        user.setUuid(uuid);
        userRepository.save(user);

        // Link de reset
        String link = frontendUrl + "/reset-password?token=" + uuid;

        // Variables Thymeleaf
        var vars = new HashMap<String, Object>();
        vars.put("name", user.getName());
        vars.put("link", link);

        // Enviar email
        emailService.sendEmail(
            user.getEmail(),
            "Restablece tu contraseña en ForestPlus",
            "contents/reset-password-content",
            vars
        );
    }
    
    public void resetPasswordWithUuid(String uuid, String newPassword) {
        UserEntity user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new UserNotFoundException("UUID_INVALIDO"));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUuid(null); // invalidar el UUID para que no se reutilice
        userRepository.save(user);
    }
}
