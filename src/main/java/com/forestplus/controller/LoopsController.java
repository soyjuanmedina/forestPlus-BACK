package com.forestplus.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.forestplus.entity.UserEntity;
import com.forestplus.exception.UserNotFoundException;
import com.forestplus.integrations.loops.LoopsService;
import com.forestplus.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import java.util.Map;

@RestController
@RequestMapping("/api/loops")
@RequiredArgsConstructor
public class LoopsController {

    private final LoopsService loopsService;
    private final UserRepository userRepository;

    // Endpoint para registrar emails en la waitlist
    @PostMapping("/waitlist")
    public boolean registerEmail(@RequestBody EmailDto emailDto) {
        try {
            // Llama al service y devuelve true o false directamente
            return loopsService.registerEmail(emailDto);
        } catch (Exception e) {
            e.printStackTrace();
            // Si ocurre cualquier error, devolvemos false
            return false;
        }
    }

    // DTO simple para recibir el email
    public static class EmailDto {
        private String email;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    // Aquí más endpoints futuros relacionados con Loops
    // Ejemplo: actualizar contacto, eliminar contacto, etc.

    /**
     * Endpoint temporal para TESTING.
     * Devuelve el UUID de verificación de un usuario por su email.
     * NO USAR EN PRODUCCIÓN.
     */
    @GetMapping("/test-get-uuid")
    public ResponseEntity<Map<String, String>> getTestUuid(@RequestParam String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return ResponseEntity.ok(Map.of("uuid", user.getUuid()));
    }
}
