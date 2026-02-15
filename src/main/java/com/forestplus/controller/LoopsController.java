package com.forestplus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.forestplus.integrations.loops.LoopsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loops")
@RequiredArgsConstructor
public class LoopsController {

    private final LoopsService loopsService;

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
}
