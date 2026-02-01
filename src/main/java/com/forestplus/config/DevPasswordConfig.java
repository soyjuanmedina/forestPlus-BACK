package com.forestplus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class DevPasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Solo para que AuthService arranque; tu filtro de contraseña simple no lo necesita
        return new BCryptPasswordEncoder();
    }
}
