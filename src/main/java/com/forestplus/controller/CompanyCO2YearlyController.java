package com.forestplus.controller;

import java.util.List;

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

@RestController
@RequestMapping("/api/companies/{companyId}/co2")
@RequiredArgsConstructor
public class CompanyCO2YearlyController {

    private final CompanyCO2YearlyService co2Service;

    @GetMapping
    public List<CompanyCO2YearlyResponse> getAll(@PathVariable Long companyId) {
        return co2Service.getAllForCompany(companyId);
    }

    @PostMapping
    public CompanyCO2YearlyResponse createOrUpdate(
            @PathVariable Long companyId,
            @RequestBody CompanyCO2YearlyRequest request
    ) {
        return co2Service.createOrUpdate(companyId, request.getYear(), request.getTotalEmissions(), request.getTotalCompensations());
    }

    @DeleteMapping("/{id}")
    public void delete(
        @PathVariable Long companyId, // 👈 esto es lo que faltaba
        @PathVariable Long id
    ) {
        co2Service.delete(id);
    }
}

