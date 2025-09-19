package com.forestplus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // para pruebas en Postman
            .authorizeHttpRequests()
                .requestMatchers("/api/auth/register").permitAll() // permitimos registro sin login
                .anyRequest().authenticated() // todo lo demás requiere autenticación
            .and()
            .httpBasic(); // habilita basic auth solo si quieres

        return http.build();
    }
}
