package com.forestplus.controller;

import com.forestplus.request.RegisterUserRequest;
import com.forestplus.request.ResendVerificationEmailRequest;
import com.forestplus.response.AuthResponse;
import com.forestplus.response.UserResponse;
import com.forestplus.entity.UserEntity;
import com.forestplus.mapper.UserMapper;
import com.forestplus.service.AuthService;
import com.forestplus.service.UserService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200") // frontend
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterUserRequest request) {
        UserEntity user = authService.register(request);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody RegisterUserRequest request) {
        String jwt = authService.login(request.getEmail(), request.getPassword());
        UserResponse user = userService.getUserByEmail(request.getEmail());
        return ResponseEntity.ok(new AuthResponse(jwt, user));
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
}
