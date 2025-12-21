package com.forestplus.controller;

import com.forestplus.dto.request.CoordinateRequest;
import com.forestplus.dto.request.CoordinateUpdateRequest;
import com.forestplus.dto.response.CoordinateResponse;
import com.forestplus.service.CoordinateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/coordinates", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CoordinateController {

    private final CoordinateService coordinateService;

    // ---------------------------------------------------------
    // LISTAR TODAS LAS COORDENADAS
    // ---------------------------------------------------------
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener todas las coordenadas")
    public ResponseEntity<List<CoordinateResponse>> getAllCoordinates() {
        return ResponseEntity.ok(coordinateService.getAllCoordinates());
    }

    // ---------------------------------------------------------
    // OBTENER COORDENADA POR ID
    // ---------------------------------------------------------
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener coordenada por ID")
    public ResponseEntity<CoordinateResponse> getCoordinateById(
            @Parameter(description = "ID de la coordenada") @PathVariable Long id) {
        return ResponseEntity.ok(coordinateService.getCoordinateById(id));
    }

    // ---------------------------------------------------------
    // CREAR NUEVA COORDENADA
    // ---------------------------------------------------------
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear una nueva coordenada")
    public ResponseEntity<CoordinateResponse> createCoordinate(
            @RequestBody CoordinateRequest request) {
        return ResponseEntity.ok(coordinateService.createCoordinate(request));
    }

    // ---------------------------------------------------------
    // ACTUALIZAR COORDENADA
    // ---------------------------------------------------------
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar coordenada existente")
    public ResponseEntity<CoordinateResponse> updateCoordinate(
            @Parameter(description = "ID de la coordenada") @PathVariable Long id,
            @RequestBody CoordinateUpdateRequest request) {
        return ResponseEntity.ok(coordinateService.updateCoordinate(id, request));
    }

    // ---------------------------------------------------------
    // BORRAR COORDENADA
    // ---------------------------------------------------------
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar coordenada por ID")
    public ResponseEntity<Void> deleteCoordinate(
            @Parameter(description = "ID de la coordenada") @PathVariable Long id) {
        coordinateService.deleteCoordinate(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------------------------------------------------
    // LISTAR COORDENADAS POR PARCELA
    // ---------------------------------------------------------
    @GetMapping("/land/{landId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener coordenadas de una parcela por ID de land")
    public ResponseEntity<List<CoordinateResponse>> getCoordinatesByLand(
            @Parameter(description = "ID de la parcela") @PathVariable Long landId) {
        return ResponseEntity.ok(coordinateService.getCoordinatesByLand(landId));
    }

    // ---------------------------------------------------------
    // BORRAR TODAS LAS COORDENADAS DE UNA PARCELA
    // ---------------------------------------------------------
    @DeleteMapping("/land/{landId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    @Operation(summary = "Eliminar todas las coordenadas de una parcela")
    public ResponseEntity<Void> deleteCoordinatesByLand(
            @Parameter(description = "ID de la parcela") @PathVariable Long landId) {
        coordinateService.deleteCoordinatesByLand(landId);
        return ResponseEntity.noContent().build();
    }
}
