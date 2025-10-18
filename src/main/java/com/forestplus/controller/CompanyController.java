package com.forestplus.controller;

import com.forestplus.dto.request.CompanyRequest;
import com.forestplus.dto.request.CompanyUpdateRequest;
import com.forestplus.dto.response.CompanyResponse;
import com.forestplus.entity.UserEntity;
import com.forestplus.repository.UserRepository;
import com.forestplus.service.CompanyService;
import com.forestplus.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/companies", produces = "application/json")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    // ============================
    // Obtener todas las compañías
    // ============================
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<List<CompanyResponse>> getAllCompanies() {
        List<CompanyResponse> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }

    // ============================
    // Obtener una compañía por ID
    // ============================
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<CompanyResponse> getCompanyById(@PathVariable Long id) {
        try {
            CompanyResponse company = companyService.getCompanyById(id);
            return ResponseEntity.ok(company);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ============================
    // Crear una nueva compañía
    // ============================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<CompanyResponse> createCompany(@RequestBody CompanyRequest request) {
        CompanyResponse created = companyService.createCompany(request);
        return ResponseEntity.ok(created);
    }

 // ============================
 // Actualizar compañía
 // ============================
 @PutMapping("/{id}")
 @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
 public ResponseEntity<CompanyResponse> updateCompany(
         @PathVariable Long id,
         @RequestBody CompanyUpdateRequest request,
         @RequestHeader(name = "Authorization", required = false) String authHeader // JWT opcional
 ) {
     // Extraemos email solo si viene el header (puede que interceptor lo agregue)
     UserEntity loggedUser;
     if (authHeader != null) {
         String email = jwtService.extractEmail(authHeader);
         loggedUser = userRepository.findByEmail(email)
                 .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
     } else {
         // Opcional: si no hay header, asumimos usuario por sesión o otra lógica
         throw new RuntimeException("No se pudo identificar el usuario");
     }

     CompanyResponse updated = companyService.updateCompany(id, request, loggedUser);
     return ResponseEntity.ok(updated);
 }


    // ============================
    // Eliminar compañía
    // ============================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        try {
            companyService.deleteCompany(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ============================
    // Subir o actualizar imagen de la compañía
    // ============================
    @PutMapping(value = "/{id}/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    @Operation(
        summary = "Actualizar la imagen de perfil de la compañía",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Archivo de imagen a subir",
            required = true,
            content = @Content(
                mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                schema = @Schema(type = "string", format = "binary")
            )
        )
    )
    @ApiResponse(
        responseCode = "200",
        description = "Imagen de compañía actualizada",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CompanyResponse.class)
        )
    )
    public ResponseEntity<CompanyResponse> updateCompanyPicture(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            CompanyResponse response = companyService.updateCompanyPicture(id, file);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
