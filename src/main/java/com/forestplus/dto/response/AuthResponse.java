package com.forestplus.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Respuesta de autenticación con token y datos del usuario")
public class AuthResponse {

    @Schema(description = "Access Token JWT del usuario")
    private String token;

    @Schema(description = "Refresh Token para renovación del access token")
    private String refreshToken;

    @Schema(description = "Información del usuario logueado")
    private UserResponse user;

    @Schema(description = "Indica si se requiere cambio de contraseña", nullable = true)
    private Boolean forcePasswordChange;
}
