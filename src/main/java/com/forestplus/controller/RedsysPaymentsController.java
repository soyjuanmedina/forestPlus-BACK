package com.forestplus.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.forestplus.dto.request.RedsysNotificationRequest;
import com.forestplus.dto.response.RedsysPaymentResponse;
import com.forestplus.service.RedsysPaymentsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/redsyspayments")
@RequiredArgsConstructor
public class RedsysPaymentsController {

    private final RedsysPaymentsService redsysPaymentsService;

    // 1️⃣ Crear pago
    @PostMapping("/create")
    public ResponseEntity<RedsysPaymentResponse> createPayment(@RequestParam Long orderId) {
        RedsysPaymentResponse response = redsysPaymentsService.generateRedsysPayment(orderId);
        return ResponseEntity.ok(response);
    }

    // 2️⃣ Notificación de Redsys
    @PostMapping("/notification")
    public ResponseEntity<String> handleNotification(@RequestBody RedsysNotificationRequest notification) {
        redsysPaymentsService.handleRedsysNotification(notification);
        return ResponseEntity.ok("OK"); // Redsys espera un OK
    }
}
