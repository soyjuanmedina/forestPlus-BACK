package com.forestplus.controller;

import com.forestplus.request.UserRequest;
import com.forestplus.response.UserResponse;
import com.forestplus.entity.UserEntity;
import com.forestplus.mapper.UserMapper;
import com.forestplus.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest request) {
        UserEntity user = authService.register(request);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserRequest request) {
        String jwt = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(jwt);
    }
}
