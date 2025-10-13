package com.forestplus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.forestplus.dto.request.RegisterUserRequest;
import com.forestplus.entity.UserEntity;
import com.forestplus.model.RolesEnum;
import com.forestplus.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // usa application-test.properties
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        UserEntity user = UserEntity.builder()
                .name("Usuario")
                .surname("Prueba")
                .email("usuario@prueba.com")
                .passwordHash(passwordEncoder.encode("123456")) // ⚡ importante: coincide con AuthService
                .role(RolesEnum.USER) // ajusta según tu rol por defecto
                .emailVerified(true) // para que pueda loguearse
                .forcePasswordChange(false)
                .build();

        userRepository.save(user);
    }

    @Test
    void login_deberiaAutenticarseCorrectamente() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("usuario@prueba.com");
        request.setPassword("123456");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())          // <- aquí estaba accessToken
                .andExpect(jsonPath("$.refreshToken").exists())   // <- correcto
                .andExpect(jsonPath("$.user.email").value("usuario@prueba.com")); // opcional, valida usuario
    }

    @Test
    void login_deberiaFallarConCredencialesInvalidas() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("usuario@prueba.com");
        request.setPassword("clave_incorrecta");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }
}
