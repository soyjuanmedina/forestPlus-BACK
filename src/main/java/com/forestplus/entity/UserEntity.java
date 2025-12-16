package com.forestplus.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.forestplus.model.RolesEnum;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor 
@NoArgsConstructor
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String surname;

    @Column(name = "second_surname")
    private String secondSurname;
    
    private String picture; 

    // Relación con compañía
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    @JsonBackReference // evita ciclo en JSON
    private CompanyEntity company;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolesEnum role;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(name = "force_password_change", nullable = false)
    @Builder.Default
    private Boolean forcePasswordChange = false;

    @Column(name = "uuid")
    private String uuid;
    
    @Builder.Default
    @Column(name = "pending_trees_count", nullable = false)
    private Integer pendingTreesCount = 0;
    
    // Relación muchos a muchos con terrenos
    @ManyToMany(mappedBy = "users")
    private List<LandEntity> lands;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
