package com.forestplus.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.forestplus.dto.response.LandTreeSummaryResponse;
import com.forestplus.entity.TreeEntity;

@Repository
public interface TreeRepository extends JpaRepository<TreeEntity, Long> {
    List<TreeEntity> findByOwnerUserId(Long userId);
    List<TreeEntity> findByOwnerCompanyId(Long companyId);
    List<TreeEntity> findByLandId(Long landId);
    
    @Query("SELECT t FROM TreeEntity t LEFT JOIN FETCH t.ownerUser u LEFT JOIN FETCH u.company LEFT JOIN FETCH t.ownerCompany WHERE t.id = :id")
    java.util.Optional<TreeEntity> findByIdWithOwners(@Param("id") Long id);
    
    long countByLandId(Long landId);

    @Query("""
	    SELECT new com.forestplus.dto.response.LandTreeSummaryResponse(
	        t.treeType.id,
	        t.treeType.name,
	        t.treeType.picture,
	        COUNT(t)
	    )
	    FROM TreeEntity t
	    WHERE t.land.id = :landId
	    GROUP BY t.treeType.id, t.treeType.name, t.treeType.picture
		""")
	List<LandTreeSummaryResponse> getTreesByLand(@Param("landId") Long landId);
    
    @Query("""
	    SELECT new com.forestplus.dto.response.LandTreeSummaryResponse(
	        t.treeType.id,
	        t.treeType.name,
	        t.treeType.picture,
	        COUNT(t)
	    )
	    FROM TreeEntity t
	    LEFT JOIN t.ownerUser u
	    LEFT JOIN t.ownerCompany c
	    WHERE 
	        (:ownerUserId IS NULL OR u.id = :ownerUserId)
	        AND (:ownerCompanyId IS NULL OR c.id = :ownerCompanyId OR (u IS NOT NULL AND u.company.id = :ownerCompanyId))
	    GROUP BY t.treeType.id, t.treeType.name, t.treeType.picture
		""")
	List<LandTreeSummaryResponse> getTreesByOwner(
			@Param("ownerUserId") Long ownerUserId,
			@Param("ownerCompanyId") Long ownerCompanyId);
    
    List<TreeEntity> findByLandIdAndOwnerUserIdIsNullAndOwnerCompanyIdIsNull(Long landId);
    
    List<TreeEntity> findByLand_IdAndTreeType_Id(Long landId, Long treeTypeId);
    
    @Query("""
    	    SELECT t FROM TreeEntity t
    	    LEFT JOIN t.ownerUser u
    	    LEFT JOIN t.ownerCompany c
    	    WHERE ((:ownerUserId IS NULL OR u.id = :ownerUserId)
    	      AND (:ownerCompanyId IS NULL OR c.id = :ownerCompanyId OR (u IS NOT NULL AND u.company.id = :ownerCompanyId)))
    	      AND t.treeType.id = :treeTypeId
    	""")
    	List<TreeEntity> findByOwnerAndType(
    	    @Param("ownerUserId") Long ownerUserId,
    	    @Param("ownerCompanyId") Long ownerCompanyId,
    	    @Param("treeTypeId") Long treeTypeId
    	);

    @Query("""
    	    SELECT t
    	    FROM TreeEntity t
    	    LEFT JOIN t.ownerUser u
    	    WHERE 
    	        (:ownerUserId IS NOT NULL AND u.id = :ownerUserId)
    	        OR
    	        (:ownerCompanyId IS NOT NULL AND (t.ownerCompany.id = :ownerCompanyId OR (u.id IS NOT NULL AND u.company.id = :ownerCompanyId)))
    	    """)
    	List<TreeEntity> findOwnerTrees(
    	        @Param("ownerUserId") Long ownerUserId,
    	        @Param("ownerCompanyId") Long ownerCompanyId
    	);
    
    @Query("""
    	    SELECT t
    	    FROM TreeEntity t
    	    LEFT JOIN t.ownerUser u
    	    WHERE 
    	        t.ownerCompany.id = :companyId
    	        OR
    	        (u.id IS NOT NULL AND u.company.id = :companyId)
    	    """)
    	List<TreeEntity> findAllByCompany(@Param("companyId") Long companyId);
    
    @Query("""
    	    SELECT COUNT(t)
    	    FROM TreeEntity t
    	    LEFT JOIN t.ownerUser u
    	    WHERE 
    	        (:userId IS NOT NULL AND u.id = :userId)
    	        OR
    	        (:companyIds IS NOT NULL AND (t.ownerCompany.id IN :companyIds OR (u.id IS NOT NULL AND u.company.id IN :companyIds)))
    	""")
    	long countOwnedTrees(
    	    @Param("userId") Long userId,
    	    @Param("companyIds") List<Long> companyIds
    	);
    
    @Query("""
    	    SELECT COALESCE(SUM(t.co2AbsorptionAt20), 0)
    	    FROM TreeEntity t
    	    LEFT JOIN t.ownerUser u
    	    WHERE 
    	        (:userId IS NOT NULL AND u.id = :userId)
    	        OR
    	        (:companyIds IS NOT NULL AND (t.ownerCompany.id IN :companyIds OR (u.id IS NOT NULL AND u.company.id IN :companyIds)))
    	""")
    	BigDecimal sumAnnualCo2At20(
    	        @Param("userId") Long userId,
    	        @Param("companyIds") List<Long> companyIds
    	);

    @Query("SELECT COALESCE(SUM(t.co2AbsorptionAt20), 0) FROM TreeEntity t")
    BigDecimal sumGlobalAnnualCo2At20();
}
