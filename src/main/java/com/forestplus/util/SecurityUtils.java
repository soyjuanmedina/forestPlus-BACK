package com.forestplus.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.forestplus.entity.UserEntity;
import com.forestplus.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
	
    private final UserRepository userRepository;

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }
    
    public Long getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();

        String email;

        if (principal instanceof org.springframework.security.core.userdetails.User user) {
            email = user.getUsername();
        } else {
            throw new RuntimeException("Tipo de principal desconocido: " + principal.getClass());
        }

        // Buscar usuario por email para obtener el ID
        return userRepository.findByEmail(email)
                .map(UserEntity::getId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
