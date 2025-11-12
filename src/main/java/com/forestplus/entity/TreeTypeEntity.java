package com.forestplus.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tree_types")
public class TreeTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description; // Nuevo campo

    @Column(name = "co2_absorption")
    private BigDecimal co2Absorption;

    @Column(name = "typical_height")
    private BigDecimal typicalHeight;

    @Column(name = "lifespan_years")
    private Integer lifespanYears;

    @Column(columnDefinition = "TEXT")
    private String picture;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
