package com.forestplus.controller;

import com.forestplus.dto.request.TreeBatchPlantRequest;
import com.forestplus.dto.request.TreeRequest;
import com.forestplus.dto.request.TreeUpdateRequest;
import com.forestplus.dto.response.LandTreeSummaryResponse;
import com.forestplus.dto.response.TreeResponse;
import com.forestplus.security.CurrentUserService;
import com.forestplus.service.AuthService;
import com.forestplus.service.TreeService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/trees", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class TreeController {

    private final TreeService treeService;
    private final AuthService authService;
    private final CurrentUserService currentUserService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TreeResponse>> getAllTrees() {
        return ResponseEntity.ok(treeService.getAllTrees());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TreeResponse> getTreeById(@PathVariable Long id) {
        return ResponseEntity.ok(treeService.getTreeById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TreeResponse> createTree(@RequestBody TreeRequest request) {
        return ResponseEntity.ok(treeService.createTree(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TreeResponse> updateTree(
            @PathVariable Long id,
            @RequestBody TreeUpdateRequest request
    ) {
        return ResponseEntity.ok(treeService.updateTree(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTree(@PathVariable Long id) {
        treeService.deleteTree(id);
        return ResponseEntity.noContent().build();
    }
    
    
    @GetMapping("/land/{landId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<List<LandTreeSummaryResponse>> getTreesByLand(@PathVariable Long landId) {
        List<LandTreeSummaryResponse> summary = treeService.getTreesByLand(landId);
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/owner")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LandTreeSummaryResponse>> getTreesByOwner(
            @RequestParam(required = false) Long ownerUserId,
            @RequestParam(required = false) Long ownerCompanyId
    ) {
        Long currentUserId = currentUserService.getCurrentUserId();
        String currentRole = currentUserService.getCurrentUserRole();

        // --- LÓGICA ---
        if ("ADMIN".equals(currentRole) || "COMPANY_ADMIN".equals(currentRole)) {
            // Admin: puede pedir cualquiera
            return ResponseEntity.ok(treeService.getTreesByOwner(ownerUserId, ownerCompanyId));
        }

        // Usuario normal: solo puede pedir sus propios árboles
        if (ownerUserId != null && !ownerUserId.equals(currentUserId)) {
            return ResponseEntity.status(403).build(); // No permitido
        }

        // Si no envía parámetros → devolver sus árboles
        return ResponseEntity.ok(treeService.getTreesByOwner(currentUserId, null));
    }
    
    @GetMapping("/owner/trees")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TreeResponse>> getTreesByOwnerAndType(
            @RequestParam(required = false) Long ownerUserId,
            @RequestParam(required = false) Long ownerCompanyId,
            @RequestParam Long treeTypeId
    ) {
        Long currentUserId = currentUserService.getCurrentUserId();
        String currentRole = currentUserService.getCurrentUserRole();

        // Admin o COMPANY_ADMIN pueden pedir cualquiera
        if ("ADMIN".equals(currentRole) || "COMPANY_ADMIN".equals(currentRole)) {
            return ResponseEntity.ok(treeService.getTreesByOwnerAndType(ownerUserId, ownerCompanyId, treeTypeId));
        }

        // Usuario normal solo sus árboles
        if (ownerUserId != null && !ownerUserId.equals(currentUserId)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(treeService.getTreesByOwnerAndType(currentUserId, null, treeTypeId));
    }

    
    @PostMapping("/plant-batch")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<?> plantTreeBatch(@RequestBody TreeBatchPlantRequest request) {
        return ResponseEntity.ok(treeService.plantTreeBatch(request));
    }
    
    @GetMapping("/unassigned")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TreeResponse>> getUnassignedTreesByLand(@RequestParam Long landId) {
        return ResponseEntity.ok(treeService.getUnassignedTreesByLand(landId));
    }
    
    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<TreeResponse> assignTreeToUser(@RequestParam Long treeId, @RequestParam Long userId) {
        return ResponseEntity.ok(treeService.assignTreeToUser(treeId, userId));
    }
    
    @PostMapping("/unassign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TreeResponse> unassignTreeFromUser(
            @RequestParam Long treeId
    ) {
        return ResponseEntity.ok(treeService.unassignTreeFromUser(treeId));
    }
    
    @PostMapping("/unassign-company")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TreeResponse> unassignTreeFromCompany(@RequestParam Long treeId) {
        return ResponseEntity.ok(treeService.unassignTreeFromCompany(treeId));
    }
    
    @GetMapping("/land/{landId}/type/{treeTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TreeResponse>> getTreesByLandAndType(
            @PathVariable Long landId,
            @PathVariable Long treeTypeId
    ) {
        return ResponseEntity.ok(treeService.getTreesByLandAndType(landId, treeTypeId));
    }
    
    @PostMapping("/assign-company")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<TreeResponse> assignTreeToCompany(
            @RequestParam Long treeId,
            @RequestParam Long companyId
    ) {
        return ResponseEntity.ok(treeService.assignTreeToCompany(treeId, companyId));
    }
    
    @GetMapping("/owner/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TreeResponse>> getAllTreesByOwner(
            @RequestParam(required = false) Long ownerUserId,
            @RequestParam(required = false) Long ownerCompanyId
    ) {
        Long currentUserId = currentUserService.getCurrentUserId();
        String currentRole = currentUserService.getCurrentUserRole();

        if ("ADMIN".equals(currentRole) || "COMPANY_ADMIN".equals(currentRole)) {
            return ResponseEntity.ok(treeService.getAllTreesByOwner(ownerUserId, ownerCompanyId));
        }

        if (ownerUserId != null && !ownerUserId.equals(currentUserId)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(treeService.getAllTreesByOwner(currentUserId, null));
    }
}
