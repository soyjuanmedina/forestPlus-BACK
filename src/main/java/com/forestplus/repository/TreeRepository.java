package com.forestplus.repository;

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
	    WHERE 
	        (:ownerUserId IS NULL OR t.ownerUser.id = :ownerUserId)
	        AND (:ownerCompanyId IS NULL OR t.ownerCompany.id = :ownerCompanyId)
	    GROUP BY t.treeType.id, t.treeType.name, t.treeType.picture
		""")
	List<LandTreeSummaryResponse> getTreesByOwner(
			@Param("ownerUserId") Long ownerUserId,
			@Param("ownerCompanyId") Long ownerCompanyId);
    
    List<TreeEntity> findByLandIdAndOwnerUserIdIsNullAndOwnerCompanyIdIsNull(Long landId);

}
