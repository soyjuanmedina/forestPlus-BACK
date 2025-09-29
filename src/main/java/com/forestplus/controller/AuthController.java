package com.forestplus.controller;

import com.forestplus.dto.request.ForgotPasswordRequest;
import com.forestplus.dto.request.RegisterUserRequest;
import com.forestplus.dto.request.ResendVerificationEmailRequest;
import com.forestplus.dto.request.ResetPasswordRequest;
import com.forestplus.dto.response.AuthResponse;
import com.forestplus.dto.response.UserResponse;
import com.forestplus.entity.UserEntity;
import com.forestplus.mapper.UserMapper;
import com.forestplus.service.AuthService;
import com.forestplus.service.JwtService;
import com.forestplus.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(
	    origins = "${app.frontend.url}",
	    allowedHeaders = "*",       // permite Authorization
	    allowCredentials = "true"
	)
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterUserRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody RegisterUserRequest request) {
        return ResponseEntity.ok(authService.login(request.getEmail(), request.getPassword()));
    }
    
    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam("uuid") String uuid) {
        try {
            authService.verifyEmail(uuid);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                                 .body(Map.of("message", "VERIFY_EMAIL.INVALID_LINK"));
        }
    }
    
    @PostMapping("/resend-verification")
    public ResponseEntity<Void> resendVerification(@RequestBody ResendVerificationEmailRequest request) {
        authService.resendVerificationEmail(request.getEmail());
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody ResetPasswordRequest request,
                              @RequestHeader("Authorization") String token) {
        // Extraer email del token JWT
        String email = jwtService.extractEmail(token); 
        authService.resetPassword(email, request);
    }
    
    @PostMapping("/forgot-password")
    public void forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
    }
    @PostMapping("/forgot-password/reset")
    public void resetForgotPassword(@RequestParam("uuid") String uuid, 
                                    @RequestBody ResetPasswordRequest request) {
        // Buscar usuario por UUID y actualizar la contraseña
        authService.resetPasswordWithUuid(uuid, request.getNewPassword()); 
    }
}
