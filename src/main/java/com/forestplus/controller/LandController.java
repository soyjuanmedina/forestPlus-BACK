package com.forestplus.controller;

import com.forestplus.dto.request.LandRequest;
import com.forestplus.dto.request.LandUpdateRequest;
import com.forestplus.dto.response.LandResponse;
import com.forestplus.service.LandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lands")
@RequiredArgsConstructor
public class LandController {

    private final LandService landService;

    @PostMapping
    public ResponseEntity<LandResponse> create(@RequestBody LandRequest request) {
        return ResponseEntity.ok(landService.createLand(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LandResponse> update(
            @PathVariable Long id,
            @RequestBody LandUpdateRequest request) {

        return ResponseEntity.ok(landService.updateLand(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LandResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(landService.getLandById(id));
    }

    @GetMapping
    public ResponseEntity<List<LandResponse>> getAll() {
        return ResponseEntity.ok(landService.getAllLands());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        landService.deleteLand(id);
        return ResponseEntity.noContent().build();
    }
}
