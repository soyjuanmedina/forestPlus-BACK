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
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())

            // 👈 IMPORTANTE: permitir sesión
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )

            // 🔐 TODO protegido por el candado
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/login",
                    "/error",
                    "/*.js",
                    "/*.css",
                    "/assets/**"
                ).permitAll()
                .anyRequest().authenticated()
            )

            // 🔐 Login de entorno
            .formLogin(form -> form
                .defaultSuccessUrl("/", true)
            )

            // 🔓 JWT sigue funcionando después
            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    // 👤 Usuario DEV (candado)
    @Bean
    public UserDetailsService devUserDetailsService(PasswordEncoder encoder) {

        return new InMemoryUserDetailsManager(
            User.withUsername("dev")
                .password(encoder.encode("clave-dev-fuerte"))
                .roles("DEV")
                .build()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
