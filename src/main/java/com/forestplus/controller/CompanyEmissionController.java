package com.forestplus.controller;

import com.forestplus.dto.request.CompanyEmissionRequest;
import com.forestplus.dto.response.CompanyEmissionResponse;
import com.forestplus.service.CompanyEmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/company-emissions", produces = "application/json")
@RequiredArgsConstructor
public class CompanyEmissionController {

    private final CompanyEmissionService emissionService;

    // ============================
    // Crear una emisión
    // ============================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<CompanyEmissionResponse> create(@RequestBody CompanyEmissionRequest request) {
        CompanyEmissionResponse created = emissionService.create(request);
        return ResponseEntity.ok(created);
    }

    // ============================
    // Obtener todas las emisiones
    // ============================
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<List<CompanyEmissionResponse>> findAll() {
        return ResponseEntity.ok(emissionService.findAll());
    }

    // ============================
    // Obtener emisiones por compañía
    // ============================
    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<List<CompanyEmissionResponse>> findByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(emissionService.findByCompany(companyId));
    }

    // ============================
    // Eliminar emisión
    // ============================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        emissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
