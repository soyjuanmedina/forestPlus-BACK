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

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    @Value("${app.frontend.url}")
    private String frontendUrl;

    public UserEntity register(RegisterUserRequest request) {
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
        String text = "Hola " + saved.getName() + ",\n\n"
                + "Por favor confirma tu cuenta haciendo clic en este enlace:\n"
                + link + "\n\n"
                + "Si no creaste esta cuenta, ignora este correo.";
        emailService.sendEmail(saved.getEmail(), subject, text);

        return saved;
    }

    public String login(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new WrongPasswordException();
        }

        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new EmailNotVerifiedException(email);
        }

        return jwtService.generateToken(user);
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
        String text = "Hola " + user.getName() + ",\n\n"
                    + "Por favor confirma tu cuenta haciendo clic en este enlace:\n"
                    + link + "\n\n"
                    + "Si no creaste esta cuenta, ignora este correo.";
        emailService.sendEmail(user.getEmail(), subject, text);
    }
}
