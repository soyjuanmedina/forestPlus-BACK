package com.forestplus.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "trees")
public class TreeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Valores de CO₂
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

    // Foto del árbol
    @Column(columnDefinition = "TEXT")
    private String picture;

    @Column(name = "planted_at")
    private LocalDate plantedAt;

    @Column(name = "scientific_name")
    private String scientificName;

    @Column(name = "custom_name")
    private String customName;

    @ManyToOne
    @JoinColumn(name = "tree_type_id", nullable = false)
    private TreeTypeEntity treeType;

    @ManyToOne
    @JoinColumn(name = "land_id", nullable = false)
    private LandEntity land;

    @ManyToOne
    @JoinColumn(name = "owner_user_id")
    private UserEntity ownerUser;

    @ManyToOne
    @JoinColumn(name = "owner_company_id")
    private CompanyEntity ownerCompany;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planned_plantation_id")
    private PlannedPlantationEntity plannedPlantation;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
