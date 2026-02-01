package com.forestplus.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@Profile("dev")    // 👈 SOLO development
@EnableMethodSecurity
@RequiredArgsConstructor
public class DevSecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated() // TODO el WAR dev protegido
            )
            .httpBasic(); // 👈 usa la ventana de login del navegador, muy simple

        return http.build();
    }

    // 👤 Usuario DEV (candado)
    @Bean
    public UserDetailsService devUserDetailsService(PasswordEncoder encoder) {

        return new InMemoryUserDetailsManager(
                User.withUsername("dev")    // usuario fijo
                .password("{noop}clave") // contraseña sin encriptar
                .roles("DEV")
                .build()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
