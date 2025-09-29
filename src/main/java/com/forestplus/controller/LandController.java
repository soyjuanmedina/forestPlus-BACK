package com.forestplus.controller;

import com.forestplus.dto.request.LandRequest;
import com.forestplus.dto.response.LandResponse;
import com.forestplus.service.LandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lands")
public class LandController {

    private final LandService landService;

    public LandController(LandService landService) {
        this.landService = landService;
    }

    // Obtener todas las tierras
    @GetMapping
    public ResponseEntity<List<LandResponse>> getAllLands() {
        List<LandResponse> lands = landService.getAllLands();
        return ResponseEntity.ok(lands);
    }

    // Obtener tierra por ID
    @GetMapping("/{id}")
    public ResponseEntity<LandResponse> getLandById(@PathVariable Long id) {
        try {
            LandResponse land = landService.getLandById(id);
            return ResponseEntity.ok(land);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Crear tierra
    @PostMapping
    public ResponseEntity<LandResponse> createLand(@RequestBody LandRequest request) {
        LandResponse created = landService.createLand(request);
        return ResponseEntity.ok(created);
    }

    // Actualizar tierra
    @PutMapping("/{id}")
    public ResponseEntity<LandResponse> updateLand(@PathVariable Long id, @RequestBody LandRequest request) {
        try {
            LandResponse updated = landService.updateLand(id, request);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar tierra
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLand(@PathVariable Long id) {
        try {
            landService.deleteLand(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
