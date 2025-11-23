package com.forestplus.service;

import com.forestplus.dto.request.TreeBatchPlantRequest;
import com.forestplus.dto.request.TreeRequest;
import com.forestplus.dto.request.TreeUpdateRequest;
import com.forestplus.dto.response.LandTreeSummaryResponse;
import com.forestplus.dto.response.TreeBatchPlantResponse;
import com.forestplus.dto.response.TreeResponse;
import com.forestplus.entity.TreeEntity;

import java.util.List;

public interface TreeService {

    List<TreeResponse> getAllTrees();

    TreeResponse getTreeById(Long id);

    TreeResponse createTree(TreeRequest request);

    TreeResponse updateTree(Long id, TreeUpdateRequest request);

    void deleteTree(Long id);
    
	List<LandTreeSummaryResponse> getTreesByLand(Long id);
	
	List<LandTreeSummaryResponse> getTreesByOwner(Long ownerUserId, Long ownerCompanyId);

	TreeBatchPlantResponse plantTreeBatch(TreeBatchPlantRequest request);

	List<TreeResponse> getUnassignedTreesByLand(Long landId);

	TreeResponse assignTreeToUser(Long treeId, Long userId);

	List<TreeResponse> getTreesByLandAndType(Long landId, Long treeTypeId);

}
