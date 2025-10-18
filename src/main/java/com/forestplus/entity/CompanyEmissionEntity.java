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
@Table(name = "company_emissions")
public class CompanyEmissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con Company
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyEntity company;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "total_emissions", nullable = false)
    private BigDecimal totalEmissions;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}
