package com.forestplus.controller;

import com.forestplus.dto.request.TreeTypeRequest;
import com.forestplus.dto.request.TreeTypeUpdateRequest;
import com.forestplus.dto.response.TreeTypeResponse;
import com.forestplus.service.TreeTypeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/tree-types", produces = "application/json")
@RequiredArgsConstructor
public class TreeTypeController {

    private final TreeTypeService treeTypeService;

    // ============================
    // Obtener todos los tipos de árboles
    // ============================
    @GetMapping
    public ResponseEntity<List<TreeTypeResponse>> getAllTreeTypes() {
        List<TreeTypeResponse> treeTypes = treeTypeService.getAllTreeTypes();
        return ResponseEntity.ok(treeTypes);
    }

    // ============================
    // Obtener un tipo de árbol por ID
    // ============================
    @GetMapping("/{id}")
    public ResponseEntity<TreeTypeResponse> getTreeTypeById(@PathVariable("id") Long id) {
        try {
            TreeTypeResponse treeType = treeTypeService.getTreeTypeById(id);
            return ResponseEntity.ok(treeType);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ============================
    // Crear un nuevo tipo de árbol
    // ============================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TreeTypeResponse> createTreeType(@RequestBody TreeTypeRequest request) {
        TreeTypeResponse created = treeTypeService.createTreeType(request);
        return ResponseEntity.ok(created);
    }

    // ============================
    // Actualizar tipo de árbol
    // ============================
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TreeTypeResponse> updateTreeType(
            @PathVariable("id") Long id,
            @RequestBody TreeTypeUpdateRequest request
    ) {
        TreeTypeResponse updated = treeTypeService.updateTreeType(id, request);
        return ResponseEntity.ok(updated);
    }

    // ============================
    // Eliminar tipo de árbol
    // ============================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTreeType(@PathVariable("id") Long id) {
        treeTypeService.deleteTreeType(id);
        return ResponseEntity.noContent().build(); // 204 si todo va bien
    }

    // ============================
    // Subir o actualizar imagen del tipo de árbol
    // ============================
    @PutMapping(value = "/{id}/picture")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Actualizar la imagen del tipo de árbol",
        description = "Recibe un JSON con el campo 'picture' conteniendo la imagen en formato Base64"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Imagen del tipo de árbol actualizada",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = TreeTypeResponse.class)
        )
    )
    public ResponseEntity<TreeTypeResponse> updateTreeTypePicture(
            @PathVariable("id") Long id,
            @RequestBody java.util.Map<String, String> body
    ) {
        try {
            String picture = body.get("picture");
            TreeTypeResponse response = treeTypeService.updateTreeTypePicture(id, picture);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = "/{id}/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar imagen del tipo de árbol (Compatible con v1 - Multipart)")
    public ResponseEntity<TreeTypeResponse> updateTreeTypePictureMultipart(
            @PathVariable("id") Long id,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String base64 = "data:" + file.getContentType() + ";base64," + 
                            java.util.Base64.getEncoder().encodeToString(file.getBytes());
            TreeTypeResponse response = treeTypeService.updateTreeTypePicture(id, base64);
            return ResponseEntity.ok(response);
        } catch (java.io.IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
