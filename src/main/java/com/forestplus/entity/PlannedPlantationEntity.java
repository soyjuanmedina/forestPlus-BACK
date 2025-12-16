package com.forestplus.entity;

import jakarta.persistence.*;
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
@Table(name = "planned_plantations")
public class PlannedPlantationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Terreno asociado (opcional)
     */
    @ManyToOne
    @JoinColumn(name = "land_id")
    private LandEntity land;

    /**
     * Fecha prevista de la plantación
     */
    @Column(name = "planned_date", nullable = false)
    private LocalDate plannedDate;

    /**
     * Fecha efectiva de la plantación (cuando se ejecuta)
     */
    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    /**
     * Número mínimo de árboles a plantar
     */
    @Column(name = "min_trees", nullable = false)
    private Integer minTrees;

    /**
     * Número óptimo de árboles a plantar (opcional)
     */
    @Column(name = "optimal_trees")
    private Integer optimalTrees;

    /**
     * Número máximo de árboles a plantar (opcional)
     */
    @Column(name = "max_trees")
    private Integer maxTrees;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = false;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
