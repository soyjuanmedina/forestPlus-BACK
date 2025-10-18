package com.forestplus.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "company_compensations")
public class CompanyCompensationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Company
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyEntity company;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "total_compensations", nullable = false)
    private BigDecimal totalCompensations;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}
