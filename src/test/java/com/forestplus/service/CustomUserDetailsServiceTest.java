package com.forestplus.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.forestplus.entity.UserEntity;
import com.forestplus.model.RolesEnum;
import com.forestplus.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    private UserEntity user;

    @BeforeEach
    void setup() {
        user = new UserEntity();
        user.setEmail("test@mail.com");
        user.setPasswordHash("encodedPassword");
    }

    // ---------------------------------------------------------
    // USER FOUND WITH ROLE
    // ---------------------------------------------------------
    @Test
    void shouldLoadUserWithRole() {
        user.setRole(RolesEnum.ADMIN);

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("test@mail.com");

        assertEquals("test@mail.com", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    // ---------------------------------------------------------
    // USER FOUND WITHOUT ROLE → DEFAULT USER
    // ---------------------------------------------------------
    @Test
    void shouldLoadUserWithDefaultRoleWhenRoleIsNull() {
        user.setRole(null);

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("test@mail.com");

        assertTrue(result.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    // ---------------------------------------------------------
    // USER NOT FOUND
    // ---------------------------------------------------------
    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("notfound@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("notfound@mail.com"));
    }
}