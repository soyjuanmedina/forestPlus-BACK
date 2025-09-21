package com.forestplus.service;

import com.forestplus.entity.UserEntity;
import com.forestplus.exception.EmailAlreadyExistsException;
import com.forestplus.repository.UserRepository;
import com.forestplus.request.UserRequest;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserEntity register(UserRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
        	throw new EmailAlreadyExistsException(request.getEmail());
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .secondSurname(request.getSecondSurname())
                .email(request.getEmail())
                .passwordHash(encodedPassword)
                .role(request.getRole())
                .build();

        return userRepository.save(user);
    }

    public String login(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return jwtService.generateToken(user); // Devuelve el JWT
    }
}
