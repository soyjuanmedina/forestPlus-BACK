package com.forestplus.service;

import com.forestplus.dto.request.TreeRequest;
import com.forestplus.dto.request.TreeUpdateRequest;
import com.forestplus.dto.response.TreeResponse;
import com.forestplus.entity.TreeEntity;
import com.forestplus.entity.TreeTypeEntity;
import com.forestplus.entity.LandEntity;
import com.forestplus.entity.UserEntity;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.repository.TreeRepository;
import com.forestplus.repository.TreeTypeRepository;
import com.forestplus.repository.LandRepository;
import com.forestplus.repository.UserRepository;
import com.forestplus.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TreeServiceImpl implements TreeService {

    private final TreeRepository treeRepository;
    private final TreeTypeRepository treeTypeRepository;
    private final LandRepository landRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Override
    public List<TreeResponse> getAllTrees() {
        return treeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TreeResponse getTreeById(Long id) {
        return treeRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Tree not found with id " + id));
    }

    @Override
    public TreeResponse createTree(TreeRequest request) {
        TreeEntity tree = new TreeEntity();
        tree.setSpecies(request.getSpecies());
        tree.setPlantedAt(request.getPlantedAt());
        tree.setCo2Absorption(request.getCo2Absorption());

        LandEntity land = landRepository.findById(request.getLandId())
                .orElseThrow(() -> new RuntimeException("Land not found"));
        tree.setLand(land);

        TreeTypeEntity type = treeTypeRepository.findById(request.getTreeTypeId())
                .orElseThrow(() -> new RuntimeException("TreeType not found"));
        tree.setTreeType(type);

        if (request.getOwnerUserId() != null) {
            UserEntity user = userRepository.findById(request.getOwnerUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            tree.setOwnerUser(user);
        }

        if (request.getOwnerCompanyId() != null) {
            CompanyEntity company = companyRepository.findById(request.getOwnerCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            tree.setOwnerCompany(company);
        }

        TreeEntity saved = treeRepository.save(tree);
        return mapToResponse(saved);
    }

    @Override
    public TreeResponse updateTree(Long id, TreeUpdateRequest request) {
        TreeEntity updated = treeRepository.findById(id)
                .map(tree -> {
                    tree.setSpecies(request.getSpecies());
                    tree.setPlantedAt(request.getPlantedAt());
                    tree.setCo2Absorption(request.getCo2Absorption());

                    if (request.getLandId() != null) {
                        LandEntity land = landRepository.findById(request.getLandId())
                                .orElseThrow(() -> new RuntimeException("Land not found"));
                        tree.setLand(land);
                    }

                    if (request.getTreeTypeId() != null) {
                        TreeTypeEntity type = treeTypeRepository.findById(request.getTreeTypeId())
                                .orElseThrow(() -> new RuntimeException("TreeType not found"));
                        tree.setTreeType(type);
                    }

                    if (request.getOwnerUserId() != null) {
                        UserEntity user = userRepository.findById(request.getOwnerUserId())
                                .orElseThrow(() -> new RuntimeException("User not found"));
                        tree.setOwnerUser(user);
                    }

                    if (request.getOwnerCompanyId() != null) {
                        CompanyEntity company = companyRepository.findById(request.getOwnerCompanyId())
                                .orElseThrow(() -> new RuntimeException("Company not found"));
                        tree.setOwnerCompany(company);
                    }

                    return treeRepository.save(tree);
                })
                .orElseThrow(() -> new RuntimeException("Tree not found with id " + id));

        return mapToResponse(updated);
    }

    @Override
    public void deleteTree(Long id) {
        treeRepository.deleteById(id);
    }

    // ====================================
    // Mapper a DTO
    // ====================================
    private TreeResponse mapToResponse(TreeEntity tree) {
        return TreeResponse.builder()
                .id(tree.getId())
                .species(tree.getSpecies())
                .co2Absorption(tree.getCo2Absorption())
                .plantedAt(tree.getPlantedAt())
                .treeTypeId(tree.getTreeType() != null ? tree.getTreeType().getId() : null)
                .treeTypeName(tree.getTreeType() != null ? tree.getTreeType().getName() : null)
                .landId(tree.getLand() != null ? tree.getLand().getId() : null)
                .landName(tree.getLand() != null ? tree.getLand().getName() : null)
                .ownerUserId(tree.getOwnerUser() != null ? tree.getOwnerUser().getId() : null)
                .ownerUserName(tree.getOwnerUser() != null ? tree.getOwnerUser().getName() : null)
                .ownerCompanyId(tree.getOwnerCompany() != null ? tree.getOwnerCompany().getId() : null)
                .ownerCompanyName(tree.getOwnerCompany() != null ? tree.getOwnerCompany().getName() : null)
                .build();
    }
}
