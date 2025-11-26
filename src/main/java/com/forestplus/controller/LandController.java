package com.forestplus.controller;

import com.forestplus.dto.request.LandRequest;
import com.forestplus.dto.request.LandUpdateRequest;
import com.forestplus.dto.response.LandResponse;
import com.forestplus.entity.UserEntity;
import com.forestplus.repository.UserRepository;
import com.forestplus.service.LandService;
import com.forestplus.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;

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
    @Operation(
        operationId = "getAllLands",
        summary = "Obtener todas las parcelas"
    )
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LandResponse>> getAllLands() {
        return ResponseEntity.ok(landService.getAllLands());
    }

    // ============================
    // Obtener parcela por ID
    // ============================
    @Operation(
        operationId = "getLandById",
        summary = "Obtener una parcela por su ID"
    )
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
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
    @Operation(
        operationId = "createLand",
        summary = "Crear una nueva parcela"
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<LandResponse> createLand(@RequestBody LandRequest request) {
        LandResponse created = landService.createLand(request);
        return ResponseEntity.ok(created);
    }

    // ============================
    // Actualizar parcela
    // ============================
    @Operation(
        operationId = "updateLand",
        summary = "Actualizar una parcela existente"
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<LandResponse> updateLand(
            @PathVariable Long id,
            @RequestBody LandUpdateRequest request,
            @RequestHeader(name = "Authorization", required = false) String authHeader
    ) {
        // Extraer usuario si viene un JWT
        if (authHeader != null) {
            String email = jwtService.extractEmail(authHeader);
            userRepository.findByEmail(email)
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
    @Operation(
        operationId = "deleteLand",
        summary = "Eliminar una parcela por su ID"
    )
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
