package com.forestplus.service;

import com.forestplus.dto.request.RegisterUserRequest;
import com.forestplus.dto.request.ResetPasswordRequest;
import com.forestplus.dto.response.AuthResponse;
import com.forestplus.dto.response.UserResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.UserEntity;
import com.forestplus.exception.EmailAlreadyExistsException;
import com.forestplus.exception.EmailAlreadyVerifiedException;
import com.forestplus.exception.EmailNotVerifiedException;
import com.forestplus.exception.ForestPlusException;
import com.forestplus.exception.UserNotFoundException;
import com.forestplus.exception.UuidNotFoundException;
import com.forestplus.exception.WrongPasswordException;
import com.forestplus.integrations.loops.LoopsService;
import com.forestplus.integrations.loops.dto.LoopsEventRequest;
import com.forestplus.mapper.UserMapper;
import com.forestplus.model.RolesEnum;
import com.forestplus.repository.CompanyRepository;
import com.forestplus.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
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
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository; // <--- nuevo
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final LoopsService loopsService;
    
    private static final int MAX_LOGIN_ERRORS = 5;
    
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
                .role(request.getRole() != null ? request.getRole() : RolesEnum.USER)
                .emailVerified(false)
                .forcePasswordChange(false)
                .uuid(verificationUuid)
                .build();

        // --- Asignar compañía si se recibe ---
        if (request.getCompanyId() != null) {
            CompanyEntity company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found with id " + request.getCompanyId()));
            user.setCompany(company);
        }

        UserEntity saved = userRepository.save(user);

        String link = frontendUrl + "verify-email?uuid=" + verificationUuid;
        
        // Enviar email de confirmación desde Loops
        Map<String, Object> contactProperties = new HashMap<>();
        contactProperties.put("firstName", user.getName());
        contactProperties.put("lastName", user.getSurname());

        loopsService.upsertContact(
            user.getEmail(),
            contactProperties
        );
        
        Map<String, Object> eventProperties = new HashMap<>();
        eventProperties.put("verificationLink", link);
        eventProperties.put("role", user.getRole().name());

        if (user.getCompany() != null) {
            eventProperties.put("companyId", user.getCompany().getId());
        }

        LoopsEventRequest loopsEvent = new LoopsEventRequest(
            user.getEmail(),
            "user_confirmation",
            eventProperties
        );

        loopsService.sendEvent(loopsEvent);

        return userMapper.toResponse(saved);
    }

    @Transactional(noRollbackFor = WrongPasswordException.class)
    public AuthResponse login(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        
        if (Boolean.TRUE.equals(user.getAccountLocked())) {
            throw new ForestPlusException(HttpStatus.FORBIDDEN, "ACCOUNT_LOCKED");
        }
        
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            user.setLoginErrorCount(user.getLoginErrorCount() + 1);
            if (user.getLoginErrorCount() >= MAX_LOGIN_ERRORS) {
                user.setAccountLocked(true);
            }
            userRepository.save(user);
            throw new WrongPasswordException();
        }
        if (!Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new EmailNotVerifiedException(email);
        }
        String token = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        user.setLastLoginAt(LocalDateTime.now());
        user.setLoginCount(user.getLoginCount() + 1);
        user.setLoginErrorCount(0);
        userRepository.save(user);
        UserResponse userResponse = userMapper.toResponse(user);
        Boolean forcePasswordChange = user.getForcePasswordChange() ? true : null;
        return new AuthResponse(token, refreshToken,userResponse, forcePasswordChange);
    }

    public void verifyEmail(String uuid) {
        UserEntity user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new UuidNotFoundException(uuid));
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    public void resendVerificationEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("USER_NOT_FOUND"));

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new EmailAlreadyVerifiedException("EMAIL_ALREADY_VERIFIED");
        }

        String verificationUuid = UUID.randomUUID().toString();
        user.setUuid(verificationUuid);
        userRepository.save(user);

        String subject = "Confirma tu correo en ForestPlus";
        String link = frontendUrl + "verify-email?uuid=" + verificationUuid;

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", user.getName());
        vars.put("link", link);

        emailService.sendEmail(user.getEmail(), subject, "contents/verify-email-content", vars);
    }

    @Transactional
    public void resetPassword(String email, ResetPasswordRequest request) {
    	
        log.info("Request to reset Password");

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        
        if (request.getCurrentPassword() != null) {
        	
            log.info("Request to reset Password to user with current password"); 
            if (!passwordEncoder.matches(
                    request.getCurrentPassword(),
                    user.getPasswordHash()
            )) {
                throw new ForestPlusException(
                    HttpStatus.BAD_REQUEST,
                    "CURRENT_PASSWORD_INVALID"
                );
            }
        }
        
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setForcePasswordChange(false);
        userRepository.save(user);
    }

    public void forgotPassword(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        String uuid = UUID.randomUUID().toString();
        user.setUuid(uuid);
        userRepository.save(user);

        String link = frontendUrl + "reset-password?token=" + uuid;
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", user.getName());
        vars.put("link", link);

        emailService.sendEmail(user.getEmail(), "Restablece tu contraseña en ForestPlus",
                "contents/reset-password-content", vars);
    }

    public void resetPasswordWithUuid(String uuid, String newPassword) {
        UserEntity user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new UserNotFoundException("UUID_INVALIDO"));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUuid(null);
        user.setLoginErrorCount(0);
        user.setAccountLocked(false);
        userRepository.save(user);
    }
}

