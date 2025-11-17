package com.forestplus.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.forestplus.dto.request.CompanyCO2YearlyRequest;
import com.forestplus.dto.response.CompanyCO2YearlyResponse;
import com.forestplus.service.CompanyCO2YearlyService;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/companies/{companyId}/co2")
@RequiredArgsConstructor
public class CompanyCO2YearlyController {

    private final CompanyCO2YearlyService co2Service;

    // ============================
    // Obtener registros CO2 anuales de una empresa
    // ============================
    @Operation(
        operationId = "getCompanyCO2YearlyList",
        summary = "Obtener la lista de registros CO2 anuales de una empresa"
    )
    @GetMapping
    public List<CompanyCO2YearlyResponse> getAll(@PathVariable Long companyId) {
        return co2Service.getAllForCompany(companyId);
    }

    // ============================
    // Crear o actualizar un registro CO2 anual
    // ============================
    @Operation(
        operationId = "createOrUpdateCompanyCO2Yearly",
        summary = "Crear o actualizar un registro CO2 anual para una empresa"
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public CompanyCO2YearlyResponse createOrUpdate(
            @PathVariable Long companyId,
            @RequestBody CompanyCO2YearlyRequest request
    ) {
        return co2Service.createOrUpdate(
            companyId,
            request.getYear(),
            request.getTotalEmissions(),
            request.getTotalCompensations()
        );
    }

    // ============================
    // Eliminar un registro CO2 anual
    // ============================
    @Operation(
        operationId = "deleteCompanyCO2Yearly",
        summary = "Eliminar un registro CO2 anual por su ID"
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public void delete(
        @PathVariable Long companyId,
        @PathVariable Long id
    ) {
        co2Service.delete(id);
    }
}
