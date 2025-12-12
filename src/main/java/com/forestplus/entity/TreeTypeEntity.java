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

    @Column(name = "scientific_name")
    private String scientificName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "co2_absorption_at_20")
    private BigDecimal co2AbsorptionAt20;

    @Column(name = "co2_absorption_at_25")
    private BigDecimal co2AbsorptionAt25;

    @Column(name = "co2_absorption_at_30")
    private BigDecimal co2AbsorptionAt30;

    @Column(name = "co2_absorption_at_35")
    private BigDecimal co2AbsorptionAt35;

    @Column(name = "co2_absorption_at_40")
    private BigDecimal co2AbsorptionAt40;

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
