package com.forestplus.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.forestplus.dto.request.PurchaseRequest;
import com.forestplus.service.PurchaseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> purchaseTrees(@RequestBody PurchaseRequest request) {
        return ResponseEntity.ok(purchaseService.processPurchase(request));
    }
}
