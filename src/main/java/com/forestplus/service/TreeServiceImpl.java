package com.forestplus.service;

import com.forestplus.dto.request.TreeBatchPlantRequest;
import com.forestplus.dto.request.TreeRequest;
import com.forestplus.dto.request.TreeUpdateRequest;
import com.forestplus.dto.response.LandTreeSummaryResponse;
import com.forestplus.dto.response.TreeBatchPlantResponse;
import com.forestplus.dto.response.TreeResponse;
import com.forestplus.entity.TreeEntity;
import com.forestplus.entity.TreeTypeEntity;
import com.forestplus.entity.LandEntity;
import com.forestplus.entity.UserEntity;
import com.forestplus.exception.ResourceNotFoundException;
import com.forestplus.mapper.TreeMapper;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.repository.TreeRepository;
import com.forestplus.repository.TreeTypeRepository;
import com.forestplus.repository.LandRepository;
import com.forestplus.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import com.forestplus.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private final TreeMapper treeMapper;

    @Override
    public List<TreeResponse> getAllTrees() {
        return treeMapper.toResponseList(treeRepository.findAll());
    }

    @Override
    public TreeResponse getTreeById(Long id) {
        return treeRepository.findById(id)
                .map(treeMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Tree not found with id " + id));
    }

    @Override
    public TreeResponse createTree(TreeRequest request) {
        TreeEntity tree = treeMapper.toEntity(request);

        tree.setLand(landRepository.findById(request.getLandId())
                .orElseThrow(() -> new RuntimeException("Land not found")));

        tree.setTreeType(treeTypeRepository.findById(request.getTreeTypeId())
                .orElseThrow(() -> new RuntimeException("TreeType not found")));

        if (request.getOwnerUserId() != null) {
            tree.setOwnerUser(userRepository.findById(request.getOwnerUserId())
                    .orElseThrow(() -> new RuntimeException("User not found")));
        }

        if (request.getOwnerCompanyId() != null) {
            tree.setOwnerCompany(companyRepository.findById(request.getOwnerCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found")));
        }

        return treeMapper.toResponse(treeRepository.save(tree));
    }

    @Override
    public TreeResponse updateTree(Long id, TreeUpdateRequest request) {
        TreeEntity tree = treeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tree not found with id " + id));

        tree.setSpecies(request.getSpecies());
        tree.setPlantedAt(request.getPlantedAt());
        tree.setCo2Absorption(request.getCo2Absorption());

        if (request.getLandId() != null) {
            tree.setLand(landRepository.findById(request.getLandId())
                    .orElseThrow(() -> new RuntimeException("Land not found")));
        }

        if (request.getTreeTypeId() != null) {
            tree.setTreeType(treeTypeRepository.findById(request.getTreeTypeId())
                    .orElseThrow(() -> new RuntimeException("TreeType not found")));
        }

        if (request.getOwnerUserId() != null) {
            tree.setOwnerUser(userRepository.findById(request.getOwnerUserId())
                    .orElseThrow(() -> new RuntimeException("User not found")));
        }

        if (request.getOwnerCompanyId() != null) {
            tree.setOwnerCompany(companyRepository.findById(request.getOwnerCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found")));
        }

        return treeMapper.toResponse(treeRepository.save(tree));
    }

    @Override
    public void deleteTree(Long id) {
        treeRepository.deleteById(id);
    }
    
    @Override
    public List<LandTreeSummaryResponse> getTreesByLand(Long id) {
        return treeRepository.getTreesByLand(id);
    }
    
    @Override
    public List<LandTreeSummaryResponse> getTreesByOwner(Long ownerUserId, Long ownerCompanyId) {
        return treeRepository.getTreesByOwner(ownerUserId, ownerCompanyId);
    }
    
    @Override
    public List<TreeResponse> getTreesByOwnerAndType(Long ownerUserId, Long ownerCompanyId, Long treeTypeId) {
        List<TreeEntity> trees = treeRepository.findByOwnerAndType(ownerUserId, ownerCompanyId, treeTypeId);
        return treeMapper.toResponseList(trees);
    }
    
    @Override
    public TreeBatchPlantResponse plantTreeBatch(TreeBatchPlantRequest request) {

        LandEntity land = landRepository.findById(request.getLandId())
                .orElseThrow(() -> new ResourceNotFoundException("Land not found"));

        TreeTypeEntity type = treeTypeRepository.findById(request.getTreeTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Tree type not found"));

        // ==============================
        // 🔹 LÓGICA DE MAX TREES
        // ==============================
        long currentTrees = treeRepository.countByLandId(land.getId());

        long available;

        if (land.getMaxTrees() == null) {
            // ✔ No hay límite de árboles
            available = Long.MAX_VALUE;
        } else {
            // ✔ Límite normal
            available = land.getMaxTrees() - currentTrees;

            if (available <= 0) {
                return new TreeBatchPlantResponse(
                        0,
                        request.getQuantity(),
                        "Land is full"
                );
            }
        }

        // ==============================
        // 🔹 Cálculo de cuántos plantar
        // ==============================
        int toPlant = (int) Math.min(request.getQuantity(), available);

        if (toPlant <= 0) {
            return new TreeBatchPlantResponse(0, request.getQuantity(), "Land is full");
        }

        // ==============================
        // 🔹 Propietarios (usuario / compañía)
        // ==============================
        UserEntity ownerUser = null;
        CompanyEntity ownerCompany = null;

        if (request.getOwnerUserId() != null) {
            ownerUser = userRepository.findById(request.getOwnerUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }

        if (request.getOwnerCompanyId() != null) {
            ownerCompany = companyRepository.findById(request.getOwnerCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        }

        // ==============================
        // 🔹 Crear los árboles
        // ==============================
        List<TreeEntity> trees = new ArrayList<>();

        for (int i = 0; i < toPlant; i++) {
            TreeEntity tree = new TreeEntity();
            tree.setLand(land);
            tree.setTreeType(type);
            tree.setPlantedAt(LocalDate.now());
            tree.setCo2Absorption(type.getCo2Absorption());

            tree.setOwnerUser(ownerUser);
            tree.setOwnerCompany(ownerCompany);

            trees.add(tree);
        }

        treeRepository.saveAll(trees);

        // ==============================
        // 🔹 Marcar terreno lleno (solo si hay máximo definido)
        // ==============================
        if (land.getMaxTrees() != null && toPlant == available) {
            land.setIsFull(true);
            landRepository.save(land);
        }

        return new TreeBatchPlantResponse(toPlant, request.getQuantity() - toPlant, "OK");
    }

    
    @Override
    public List<TreeResponse> getUnassignedTreesByLand(Long landId) {
        return treeRepository.findByLandIdAndOwnerUserIdIsNullAndOwnerCompanyIdIsNull(landId)
                             .stream()
                             .map(treeMapper::toResponse)
                             .collect(Collectors.toList());
    }
    
    @Override
    public TreeResponse assignTreeToUser(Long treeId, Long userId) {
        TreeEntity tree = treeRepository.findById(treeId)
                .orElseThrow(() -> new ResourceNotFoundException("Tree not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        tree.setOwnerUser(user);
        treeRepository.save(tree);

        return treeMapper.toResponse(tree);
    }
    
    @Override
    public TreeResponse unassignTreeFromUser(Long treeId) {
    	TreeEntity tree = treeRepository.findById(treeId)
                .orElseThrow(() -> new RuntimeException("Tree not found"));

        tree.setOwnerUser(null);   // 👈 QUITAMOS EL USUARIO ASIGNADO

        treeRepository.save(tree);

        return treeMapper.toResponse(tree);
    }
    
    @Override
    public TreeResponse unassignTreeFromCompany(Long treeId) {
    	TreeEntity tree = treeRepository.findById(treeId)
                .orElseThrow(() -> new RuntimeException("Tree not found"));

    	tree.setOwnerCompany(null);

        treeRepository.save(tree);

        return treeMapper.toResponse(tree);
    }
    
    @Override
    public List<TreeResponse> getTreesByLandAndType(Long landId, Long treeTypeId) {
        List<TreeEntity> trees = treeRepository.findByLand_IdAndTreeType_Id(landId, treeTypeId);

        return trees.stream()
                .map(treeMapper::toResponse)
                .toList();
    }
    
    @Override
    @Transactional
    public TreeResponse assignTreeToCompany(Long treeId, Long companyId) {
        // 1. Buscar el árbol
        TreeEntity tree = treeRepository.findById(treeId)
                .orElseThrow(() -> new EntityNotFoundException("Tree not found with id: " + treeId));

        // 2. Validar que no esté ya asignado a otro usuario o compañía si es necesario
        if (tree.getOwnerUser() != null || tree.getOwnerCompany() != null) {
            throw new IllegalStateException("Tree is already assigned");
        }

        // 3. Buscar la compañía
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + companyId));

        // 4. Asignar
        tree.setOwnerCompany(company);

        // 5. Guardar cambios
        treeRepository.save(tree);

        // 6. Devolver DTO
        return treeMapper.toResponse(tree);
    }
    
}

