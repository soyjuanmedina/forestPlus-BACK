package com.forestplus.controller;

import com.forestplus.dto.request.LandRequest;
import com.forestplus.dto.request.LandUpdateRequest;
import com.forestplus.dto.response.LandResponse;
import com.forestplus.entity.UserEntity;
import com.forestplus.repository.UserRepository;
import com.forestplus.service.LandService;
import com.forestplus.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/lands", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class LandController {

    private final LandService landService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    // ============================
    // Obtener todas las parcelas
    // ============================
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<List<LandResponse>> getAllLands() {
        List<LandResponse> lands = landService.getAllLands();
        return ResponseEntity.ok(lands);
    }

    // ============================
    // Obtener parcela por ID
    // ============================
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<LandResponse> getLandById(@PathVariable Long id) {
        try {
            LandResponse land = landService.getLandById(id);
            return ResponseEntity.ok(land);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ============================
    // Crear nueva parcela
    // ============================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<LandResponse> createLand(@RequestBody LandRequest request) {
        LandResponse created = landService.createLand(request);
        return ResponseEntity.ok(created);
    }

    // ============================
    // Actualizar parcela
    // ============================
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<LandResponse> updateLand(
            @PathVariable Long id,
            @RequestBody LandUpdateRequest request,
            @RequestHeader(name = "Authorization", required = false) String authHeader
    ) {
        // Extraer usuario si viene JWT
        UserEntity loggedUser;
        if (authHeader != null) {
            String email = jwtService.extractEmail(authHeader);
            loggedUser = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        } else {
            throw new RuntimeException("No se pudo identificar el usuario");
        }

        LandResponse updated = landService.updateLand(id, request);
        return ResponseEntity.ok(updated);
    }

    // ============================
    // Eliminar parcela
    // ============================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<Void> deleteLand(@PathVariable Long id) {
        try {
            landService.deleteLand(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
