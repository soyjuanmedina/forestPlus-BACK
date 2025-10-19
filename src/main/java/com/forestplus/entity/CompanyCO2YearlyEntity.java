package com.forestplus.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "company_co2_yearly", uniqueConstraints = @UniqueConstraint(columnNames = {"company_id", "year"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyCO2YearlyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "total_emissions", nullable = false)
    private BigDecimal totalEmissions = BigDecimal.ZERO;

    @Column(name = "total_compensations", nullable = false)
    private BigDecimal totalCompensations = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, insertable = false, updatable = false)
    private CompanyEntity company;
}
