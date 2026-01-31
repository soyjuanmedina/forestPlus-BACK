package com.forestplus.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "lands")
public class LandEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    private String location;

    private BigDecimal area;

    private String picture;

    @OneToMany(mappedBy = "land", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CoordinateEntity> coordinates;

    // Relación muchos a muchos con compañías
    @ManyToMany(mappedBy = "lands")
    private List<CompanyEntity> companies;

    // Relación muchos a muchos con usuarios
    @ManyToMany
    @JoinTable(
        name = "user_lands",
        joinColumns = @JoinColumn(name = "land_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<UserEntity> users;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 🔥 NUEVO: Máximo de árboles permitidos en este terreno
    @Column(name = "max_trees")
    private Integer maxTrees;

    // 🔥 NUEVO: Indicador de si el terreno está lleno
    @Column(name = "is_full")
    private Boolean isFull;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
