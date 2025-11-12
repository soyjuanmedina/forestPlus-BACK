package com.forestplus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import com.forestplus.dto.request.RegisterUserRequest;
import com.forestplus.entity.UserEntity;
import com.forestplus.model.RolesEnum;
import com.forestplus.repository.UserRepository;
import com.forestplus.service.EmailService;
import com.forestplus.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    private JwtService jwtService;

    private UserEntity existingUser;
    
    @MockitoBean
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        existingUser = UserEntity.builder()
                .name("Usuario")
                .surname("Prueba")
                .email("usuario@prueba.com")
                .passwordHash(passwordEncoder.encode("123456"))
                .role(RolesEnum.USER)
                .emailVerified(true)
                .forcePasswordChange(false)
                .build();

        userRepository.save(existingUser);
    }

    // -----------------------------------
    // LOGIN
    // -----------------------------------
    @Test
    void login_deberiaAutenticarseCorrectamente() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail(existingUser.getEmail());
        request.setPassword("123456");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.user.email").value(existingUser.getEmail()));
    }

    @Test
    void login_deberiaFallarConCredencialesInvalidas() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail(existingUser.getEmail());
        request.setPassword("clave_incorrecta");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    // -----------------------------------
    // REGISTER
    // -----------------------------------
    @Test
    void register_deberiaRegistrarUsuarioCorrectamente() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail("nuevo@usuario.com");
        request.setPassword("123456");
        request.setName("Nuevo");
        request.setSurname("Usuario");

        // Evitar el envío de correo
        doNothing().when(emailService).sendVerificationEmail(any(), any(), any());

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("nuevo@usuario.com"));
    }

    @Test
    void register_deberiaFallarConEmailExistente() throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setEmail(existingUser.getEmail());
        request.setPassword("123456");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // -----------------------------------
    // VERIFY EMAIL
    // -----------------------------------
    @Test
    void verifyEmail_deberiaFuncionarConUUIDValido() throws Exception {
        // 1️⃣ Crear usuario en la DB de prueba con UUID
        UserEntity user = UserEntity.builder()
                .name("Test")
                .surname("User")
                .email("test@user.com")
                .passwordHash(passwordEncoder.encode("123456"))
                .role(RolesEnum.USER)
                .emailVerified(false)
                .forcePasswordChange(false)
                .uuid("valid-uuid-simulado")
                .build();
        userRepository.save(user);

        // 2️⃣ Hacer la petición con ese UUID
        mockMvc.perform(get("/api/auth/verify")
                        .param("uuid", "valid-uuid-simulado")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Email verificado correctamente"));

        // 3️⃣ Opcional: comprobar que se actualizó emailVerified en la DB
        UserEntity updated = userRepository.findByEmail("test@user.com").orElseThrow();
        assertTrue(updated.getEmailVerified());
    }

    @Test
    void verifyEmail_deberiaFallarConUUIDInvalido() throws Exception {
        String uuidInvalido = "uuid-que-no-existe";

        mockMvc.perform(get("/api/auth/verify")
                        .param("uuid", uuidInvalido)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("VERIFY_EMAIL.INVALID_LINK"));
    }

    // -----------------------------------
    // REFRESH TOKEN
    // -----------------------------------
    @Test
    void refreshToken_deberiaGenerarNuevosTokens() throws Exception {
        String refreshToken = jwtService.generateRefreshToken(existingUser);

        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.user.email").value(existingUser.getEmail()));
    }

    @Test
    void refreshToken_deberiaFallarConTokenInvalido() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", "token-invalido");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }
}
