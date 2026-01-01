package com.forestplus.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usuario que realiza el pedido (puede ser null si es empresa)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // Empresa que realiza el pedido (puede ser null si es usuario)
    @ManyToOne
    @JoinColumn(name = "company_id")
    private CompanyEntity company;

    // Importe total del pedido
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    // Estado del pedido: pending, paid, failed
    @Column(name = "status", nullable = false)
    private String status;

    // Fecha de creación
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
