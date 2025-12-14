package com.forestplus.controller;

import com.forestplus.dto.request.PlannedPlantationRequest;
import com.forestplus.dto.request.PlannedPlantationUpdateRequest;
import com.forestplus.dto.response.PlannedPlantationResponse;
import com.forestplus.security.CurrentUserService;
import com.forestplus.service.PlannedPlantationService;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/api/planned-plantations", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PlannedPlantationController {

    private final PlannedPlantationService plannedPlantationService;
    private final CurrentUserService currentUserService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PlannedPlantationResponse>> getAll() {
        return ResponseEntity.ok(plannedPlantationService.getAllPlannedPlantations());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlannedPlantationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(plannedPlantationService.getPlannedPlantationById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlannedPlantationResponse> create(@RequestBody PlannedPlantationRequest request) {
        return ResponseEntity.ok(plannedPlantationService.createPlannedPlantation(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlannedPlantationResponse> update(
            @PathVariable Long id,
            @RequestBody PlannedPlantationUpdateRequest request
    ) {
        return ResponseEntity.ok(plannedPlantationService.updatePlannedPlantation(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        plannedPlantationService.deletePlannedPlantation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/land/{landId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlannedPlantationResponse>> getByLand(@PathVariable Long landId) {
        return ResponseEntity.ok(plannedPlantationService.getPlannedPlantationsByLand(landId));
    }

    @GetMapping("/without-land")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PlannedPlantationResponse>> getWithoutLand() {
        return ResponseEntity.ok(plannedPlantationService.getPlannedPlantationsWithoutLand());
    }

    @GetMapping("/between-dates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PlannedPlantationResponse>> getBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return ResponseEntity.ok(plannedPlantationService.getPlannedPlantationsBetweenDates(start, end));
    }

    @GetMapping("/executed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PlannedPlantationResponse>> getExecuted() {
        return ResponseEntity.ok(plannedPlantationService.getExecutedPlannedPlantations());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PlannedPlantationResponse>> getPending() {
        return ResponseEntity.ok(plannedPlantationService.getPendingPlannedPlantations());
    }
}
