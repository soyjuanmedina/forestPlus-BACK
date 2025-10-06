package com.forestplus.controller;

import com.forestplus.dto.request.ForgotPasswordRequest;
import com.forestplus.dto.request.RegisterUserRequest;
import com.forestplus.dto.request.ResendVerificationEmailRequest;
import com.forestplus.dto.request.ResetPasswordRequest;
import com.forestplus.dto.response.AuthResponse;
import com.forestplus.dto.response.MessageResponse;
import com.forestplus.dto.response.UserResponse;
import com.forestplus.service.AuthService;
import com.forestplus.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(
        origins = "${app.frontend.url}",
        allowedHeaders = "*",
        allowCredentials = "true"
)
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @Operation(
        summary = "Registro de usuario",
        description = "Registra un nuevo usuario en el sistema",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Usuario registrado correctamente",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UserResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Datos de registro inválidos",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
        }
    )
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> register(
        @Parameter(description = "Datos del usuario a registrar") 
        @RequestBody RegisterUserRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
        summary = "Login de usuario",
        description = "Autentica al usuario y devuelve token y datos del usuario",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Login exitoso",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AuthResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Credenciales inválidas",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
        }
    )
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> login(
        @Parameter(description = "Email y contraseña del usuario") 
        @RequestBody RegisterUserRequest request
    ) {
        return ResponseEntity.ok(authService.login(request.getEmail(), request.getPassword()));
    }

    @Operation(
        summary = "Verificación de email",
        description = "Verifica la cuenta del usuario usando el UUID enviado por email",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Email verificado correctamente",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Link de verificación inválido",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponse.class)
                )
            )
        }
    )
    @GetMapping(value = "/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> verifyEmail(
        @Parameter(description = "UUID enviado al email del usuario")
        @RequestParam("uuid") String uuid
    ) {
        try {
            authService.verifyEmail(uuid);
            return ResponseEntity.ok(new MessageResponse("Email verificado correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                                 .body(new MessageResponse("VERIFY_EMAIL.INVALID_LINK"));
        }
    }

    @Operation(
        summary = "Reenvío de email de verificación",
        description = "Envía de nuevo el email de verificación",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Email enviado correctamente",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponse.class)
                )
            )
        }
    )
    @PostMapping(value = "/resend-verification", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> resendVerification(
        @Parameter(description = "Email del usuario") 
        @RequestBody ResendVerificationEmailRequest request
    ) {
        authService.resendVerificationEmail(request.getEmail());
        return ResponseEntity.ok(new MessageResponse("Email enviado correctamente"));
    }

    @Operation(
        summary = "Restablecimiento de contraseña",
        description = "Cambia la contraseña del usuario autenticado",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Contraseña actualizada correctamente",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponse.class)
                )
            )
        }
    )
    @PostMapping(value = "/reset-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> resetPassword(
        @RequestBody ResetPasswordRequest request,
        @Parameter(description = "Token JWT del usuario") 
        @RequestHeader("Authorization") String token
    ) {
        String email = jwtService.extractEmail(token); 
        authService.resetPassword(email, request);
        return ResponseEntity.ok(new MessageResponse("Contraseña actualizada correctamente"));
    }

    @Operation(
        summary = "Recuperar contraseña",
        description = "Envía un email para recuperar la contraseña",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Email de recuperación enviado",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponse.class)
                )
            )
        }
    )
    @PostMapping(value = "/forgot-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> forgotPassword(
        @Parameter(description = "Email del usuario que olvidó la contraseña")
        @RequestBody ForgotPasswordRequest request
    ) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(new MessageResponse("Email de recuperación enviado"));
    }

    @Operation(
        summary = "Restablecer contraseña olvidada",
        description = "Actualiza la contraseña usando el UUID enviado por email",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Contraseña restablecida correctamente",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MessageResponse.class)
                )
            )
        }
    )
    @PostMapping(value = "/forgot-password/reset", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageResponse> resetForgotPassword(
        @Parameter(description = "UUID enviado al email") 
        @RequestParam("uuid") String uuid,
        @Parameter(description = "Nueva contraseña") 
        @RequestBody ResetPasswordRequest request
    ) {
        authService.resetPasswordWithUuid(uuid, request.getNewPassword()); 
        return ResponseEntity.ok(new MessageResponse("Contraseña restablecida correctamente"));
    }
}
