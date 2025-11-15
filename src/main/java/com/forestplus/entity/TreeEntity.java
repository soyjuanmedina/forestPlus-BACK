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

	    @Column(name = "co2_absorption", nullable = false)
	    private BigDecimal co2Absorption;

	    @Column(name = "planted_at")
	    private LocalDate plantedAt;

	    @Column(name = "species")
	    private String species;

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

	    @PrePersist
	    public void prePersist() {
	        createdAt = LocalDateTime.now();
	    }
}
