package com.forestplus.service;

import com.forestplus.entity.TreeEntity;
import com.forestplus.repository.TreeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TreeServiceImpl implements TreeService {

    private final TreeRepository treeRepository;

    public TreeServiceImpl(TreeRepository treeRepository) {
        this.treeRepository = treeRepository;
    }

    @Override
    public List<TreeEntity> getAllTrees() {
        return treeRepository.findAll();
    }

    @Override
    public Optional<TreeEntity> getTreeById(Long id) {
        return treeRepository.findById(id);
    }

    @Override
    public TreeEntity createTree(TreeEntity tree) {
        return treeRepository.save(tree);
    }

    @Override
    public TreeEntity updateTree(Long id, TreeEntity tree) {
        return treeRepository.findById(id)
                .map(existing -> {
                    existing.setSpecies(tree.getSpecies());
                    existing.setPlantedAt(tree.getPlantedAt());
                    existing.setCo2Absorption(tree.getCo2Absorption());
                    existing.setLand(tree.getLand());
                    return treeRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Tree not found with id " + id));
    }

    @Override
    public void deleteTree(Long id) {
        treeRepository.deleteById(id);
    }
}
