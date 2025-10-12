package com.forestplus.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.forestplus.dto.request.RegisterUserByAdminRequest;
import com.forestplus.dto.request.RegisterUserRequest;
import com.forestplus.dto.response.UserResponse;
import com.forestplus.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/users", produces = "application/json")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMPANY_ADMIN')")
    @Operation(summary = "Obtener usuarios con filtros, paginación y orden")
    public ResponseEntity<Page<UserResponse>> getUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort // un solo string "campo,direccion"
    ) {
        String[] parts = sort.split(",");
        String property = parts[0];
        Sort.Direction direction = parts.length > 1
                ? Sort.Direction.fromString(parts[1])
                : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(new Sort.Order(direction, property)));

        Page<UserResponse> users = userService.getUsers(pageable, role, companyId);
        return ResponseEntity.ok(users);
    }

    // =====================================
    // Obtener un usuario por id
    // =====================================
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener un usuario por su ID")
    @ApiResponse(
        responseCode = "200",
        description = "Usuario encontrado",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UserResponse.class)
        )
    )
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    // =====================================
    // Registrar usuario por admin
    // =====================================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Registrar un usuario por un administrador")
    @ApiResponse(
        responseCode = "200",
        description = "Usuario registrado",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UserResponse.class)
        )
    )
    public ResponseEntity<UserResponse> registerUserByAdmin(@RequestBody RegisterUserByAdminRequest request) {
        UserResponse response = userService.registerUserByAdmin(request);
        return ResponseEntity.ok(response);
    }

    // =====================================
    // Actualizar usuario por admin
    // =====================================
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar un usuario por un administrador")
    @ApiResponse(
        responseCode = "200",
        description = "Usuario actualizado",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UserResponse.class)
        )
    )
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<UserResponse> updateUserByAdmin(
            @PathVariable Long id,
            @RequestBody RegisterUserByAdminRequest request) {
        try {
            UserResponse response = userService.updateUserByAdmin(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // =====================================
    // Actualizar propio usuario
    // =====================================
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Actualizar el propio usuario")
    @ApiResponse(
        responseCode = "200",
        description = "Usuario actualizado",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UserResponse.class)
        )
    )
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody RegisterUserRequest request) {
        try {
            UserResponse response = userService.updateUser(id, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // =====================================
    // Eliminar usuario
    // =====================================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar un usuario")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
