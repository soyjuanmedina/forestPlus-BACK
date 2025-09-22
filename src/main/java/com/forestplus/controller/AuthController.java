package com.forestplus.controller;

import com.forestplus.request.RegisterUserRequest;
import com.forestplus.response.AuthResponse;
import com.forestplus.response.UserResponse;
import com.forestplus.entity.UserEntity;
import com.forestplus.mapper.UserMapper;
import com.forestplus.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200") // frontend
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterUserRequest request) {
        UserEntity user = authService.register(request);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody RegisterUserRequest request) {
        String jwt = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
