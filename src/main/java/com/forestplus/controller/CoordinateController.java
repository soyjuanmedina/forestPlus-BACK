package com.forestplus.controller;

import com.forestplus.entity.CoordinateEntity;
import com.forestplus.service.CoordinateService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/coordinates", produces = "application/json")
@RequiredArgsConstructor
public class CoordinateController {

    private final CoordinateService coordinateService;

    @GetMapping
    public List<CoordinateEntity> getAllCoordinates() {
        return coordinateService.getAllCoordinates();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoordinateEntity> getCoordinateById(@PathVariable Long id) {
        return coordinateService.getCoordinateById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CoordinateEntity createCoordinate(@RequestBody CoordinateEntity coordinate) {
        return coordinateService.createCoordinate(coordinate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoordinateEntity> updateCoordinate(@PathVariable Long id, @RequestBody CoordinateEntity coordinate) {
        try {
            return ResponseEntity.ok(coordinateService.updateCoordinate(id, coordinate));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoordinate(@PathVariable Long id) {
        coordinateService.deleteCoordinate(id);
        return ResponseEntity.noContent().build();
    }
}
