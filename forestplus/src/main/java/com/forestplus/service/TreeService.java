package com.forestplus.service;

import com.forestplus.entity.TreeEntity;

import java.util.List;
import java.util.Optional;

public interface TreeService {
    List<TreeEntity> getAllTrees();
    Optional<TreeEntity> getTreeById(Long id);
    TreeEntity createTree(TreeEntity tree);
    TreeEntity updateTree(Long id, TreeEntity tree);
    void deleteTree(Long id);
}
