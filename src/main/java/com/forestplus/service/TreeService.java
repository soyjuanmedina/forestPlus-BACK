package com.forestplus.service;

import com.forestplus.dto.request.TreeRequest;
import com.forestplus.dto.request.TreeUpdateRequest;
import com.forestplus.dto.response.TreeResponse;
import com.forestplus.entity.TreeEntity;

import java.util.List;

public interface TreeService {

    List<TreeResponse> getAllTrees();

    TreeResponse getTreeById(Long id);

    TreeResponse createTree(TreeRequest request);

    TreeResponse updateTree(Long id, TreeUpdateRequest request);

    void deleteTree(Long id);
}
