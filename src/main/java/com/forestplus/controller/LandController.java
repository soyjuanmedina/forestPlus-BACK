package com.forestplus.controller;

import com.forestplus.entity.LandEntity;
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

    @GetMapping
    public List<LandEntity> getAllLands() {
        return landService.getAllLands();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LandEntity> getLandById(@PathVariable Long id) {
        return landService.getLandById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public LandEntity createLand(@RequestBody LandEntity land) {
        return landService.createLand(land);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LandEntity> updateLand(@PathVariable Long id, @RequestBody LandEntity land) {
        try {
            return ResponseEntity.ok(landService.updateLand(id, land));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLand(@PathVariable Long id) {
        landService.deleteLand(id);
        return ResponseEntity.noContent().build();
    }
}
