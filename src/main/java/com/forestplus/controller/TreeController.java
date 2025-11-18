package com.forestplus.controller;

import com.forestplus.dto.request.TreeBatchPlantRequest;
import com.forestplus.dto.request.TreeRequest;
import com.forestplus.dto.request.TreeUpdateRequest;
import com.forestplus.dto.response.LandTreeSummaryResponse;
import com.forestplus.dto.response.TreeResponse;
import com.forestplus.service.TreeService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trees")
@RequiredArgsConstructor
public class TreeController {

    private final TreeService treeService;

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
    
    @PostMapping("/plant-batch")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<?> plantTreeBatch(@RequestBody TreeBatchPlantRequest request) {
        return ResponseEntity.ok(treeService.plantTreeBatch(request));
    }
}
