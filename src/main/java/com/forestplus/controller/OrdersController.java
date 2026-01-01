package com.forestplus.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.forestplus.dto.request.OrderRequest;
import com.forestplus.dto.response.OrderResponse;
import com.forestplus.entity.OrderEntity;
import com.forestplus.repository.OrdersRepository;
import com.forestplus.repository.UserRepository;
import com.forestplus.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {

        OrderEntity order = OrderEntity.builder()
                .user(request.getUserId() != null ? userRepository.findById(request.getUserId()).orElse(null) : null)
                .company(request.getCompanyId() != null ? companyRepository.findById(request.getCompanyId()).orElse(null) : null)
                .totalAmount(request.getTotalAmount())
                .status("pending")
                .build();

        order = ordersRepository.save(order);

        OrderResponse response = OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .companyId(order.getCompany() != null ? order.getCompany().getId() : null)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .build();

        return ResponseEntity.ok(response);
    }
}
