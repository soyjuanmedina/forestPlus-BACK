package com.forestplus.service;

import com.forestplus.dto.request.RegisterUserRequest;
import com.forestplus.dto.request.ResetPasswordRequest;
import com.forestplus.dto.response.AuthResponse;
import com.forestplus.dto.response.UserResponse;
import com.forestplus.entity.UserEntity;
import com.forestplus.exception.EmailAlreadyExistsException;
import com.forestplus.exception.UserNotFoundException;
import com.forestplus.exception.WrongPasswordException;
import com.forestplus.exception.ForestPlusException;
import com.forestplus.repository.CompanyRepository;
import com.forestplus.repository.UserRepository;
import com.forestplus.security.RateLimitService;
import com.forestplus.service.AuthService;
import com.forestplus.service.EmailService;
import com.forestplus.service.JwtService;
import com.forestplus.integrations.loops.LoopsService;
import com.forestplus.mapper.UserMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private LoopsService loopsService;
    @Mock
    private RateLimitService rateLimitService;

    @InjectMocks
    private AuthService authService;

    // ---------------- REGISTER ----------------

    @Test
    void shouldThrowIfEmailAlreadyExists() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("test@test.com");

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(new UserEntity()));

        assertThrows(EmailAlreadyExistsException.class,
                () -> authService.register(request));

        verify(userRepository, never()).save(any());
    }

    // ---------------- LOGIN ----------------

    @Test
    void shouldThrowIfUserNotFound() {
        when(userRepository.findByEmail("no@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> authService.login("no@test.com", "1234"));
    }

    @Test
    void shouldIncreaseLoginErrorsWhenPasswordIsWrong() {

        UserEntity user = UserEntity.builder()
                .email("test@test.com")
                .passwordHash("encoded")
                .loginErrorCount(0)
                .accountLocked(false)
                .build();

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong", "encoded"))
                .thenReturn(false);

        assertThrows(WrongPasswordException.class,
                () -> authService.login("test@test.com", "wrong"));

        assertEquals(1, user.getLoginErrorCount());
        verify(userRepository).save(user);
    }

    @Test
    void shouldLoginSuccessfully() {

        UserEntity user = UserEntity.builder()
                .email("test@test.com")
                .passwordHash("encoded")
                .emailVerified(true)
                .loginErrorCount(0)
                .loginCount(0)
                .accountLocked(false)
                .build();

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("1234", "encoded"))
                .thenReturn(true);

        when(jwtService.generateAccessToken(user))
                .thenReturn("token");

        when(jwtService.generateRefreshToken(user))
                .thenReturn("refresh");

        when(userMapper.toResponse(user))
                .thenReturn(new UserResponse());

        AuthResponse response = authService.login("test@test.com", "1234");

        assertNotNull(response);
        assertEquals("token", response.getToken());
        verify(userRepository).save(user);
    }

    // ---------------- RESET PASSWORD ----------------

    @Test
    void shouldThrowIfCurrentPasswordInvalid() {

        UserEntity user = UserEntity.builder()
                .email("test@test.com")
                .passwordHash("encoded")
                .build();

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setCurrentPassword("wrong");
        request.setNewPassword("new");

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong", "encoded"))
                .thenReturn(false);

        assertThrows(ForestPlusException.class,
                () -> authService.resetPassword("test@test.com", request));
    }
}