package com.forestplus.controller;

import com.forestplus.dto.request.CompanyCompensationRequest;
import com.forestplus.dto.response.CompanyCompensationResponse;
import com.forestplus.service.CompanyCompensationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/company-compensations", produces = "application/json")
@RequiredArgsConstructor
public class CompanyCompensationController {

    private final CompanyCompensationService compensationService;

    // ============================
    // Crear una compensación
    // ============================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<CompanyCompensationResponse> create(@RequestBody CompanyCompensationRequest request) {
        CompanyCompensationResponse created = compensationService.create(request);
        return ResponseEntity.ok(created);
    }

    // ============================
    // Obtener todas las compensaciones
    // ============================
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<List<CompanyCompensationResponse>> findAll() {
        return ResponseEntity.ok(compensationService.findAll());
    }

    // ============================
    // Obtener compensaciones por compañía
    // ============================
    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<List<CompanyCompensationResponse>> findByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(compensationService.findByCompany(companyId));
    }

    // ============================
    // Eliminar compensación
    // ============================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        compensationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
