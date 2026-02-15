package com.forestplus.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.forestplus.dto.request.TreeTypeRequest;
import com.forestplus.dto.request.TreeTypeUpdateRequest;
import com.forestplus.dto.response.TreeTypeResponse;
import com.forestplus.entity.TreeTypeEntity;
import com.forestplus.exception.ForestPlusException;
import com.forestplus.mapper.TreeTypeMapper;
import com.forestplus.repository.TreeTypeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TreeTypeServiceImpl implements TreeTypeService {

    private final TreeTypeRepository treeTypeRepository;
    private final TreeTypeMapper treeTypeMapper;
    private final FileStorageService fileStorageService; // Para manejo de imágenes
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<TreeTypeResponse> getAllTreeTypes() {
        return treeTypeRepository.findAll()
                .stream()
                .map(treeTypeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TreeTypeResponse getTreeTypeById(Long id) {
    	TreeTypeEntity entity = treeTypeRepository.findById(id)
    		    .orElseThrow(() -> new ForestPlusException(HttpStatus.NOT_FOUND,
    		        "No se encontró el tipo de árbol con id " + id) {});
        return treeTypeMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public TreeTypeResponse createTreeType(TreeTypeRequest request) {
        TreeTypeEntity entity = treeTypeMapper.toEntity(request);
        TreeTypeEntity saved = treeTypeRepository.save(entity);
        return treeTypeMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TreeTypeResponse updateTreeType(Long id, TreeTypeUpdateRequest request) {
    	TreeTypeEntity entity = treeTypeRepository.findById(id)
    		    .orElseThrow(() -> new ForestPlusException(HttpStatus.NOT_FOUND,
    		        "No se encontró el tipo de árbol con id " + id) {});

        // Mapper actualiza la entidad existente con los datos del DTO
        treeTypeMapper.updateEntityFromDto(request, entity);

        TreeTypeEntity updated = treeTypeRepository.save(entity);

        return treeTypeMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteTreeType(Long id) {
        if (!treeTypeRepository.existsById(id)) {
            throw new ForestPlusException(HttpStatus.NOT_FOUND,
                "No se encontró el tipo de árbol con id " + id) {};
        }
        try {
            treeTypeRepository.deleteById(id);
            entityManager.flush();
        } catch (RuntimeException ex) {
            Throwable root = ex;
            while (root != null) {
                if ((root instanceof org.hibernate.exception.ConstraintViolationException ||
                     root instanceof java.sql.SQLIntegrityConstraintViolationException) &&
                    root.getMessage().contains("fk_tree_type")) {
                    throw new ForestPlusException(HttpStatus.BAD_REQUEST,
                        "TREE_TYPE.DELETE_ERROR") {};
                }
                root = root.getCause();
            }
            throw ex; // cualquier otra excepción la relanza
        }
    }
    
   
    @Override
    @Transactional
    public TreeTypeResponse updateTreeTypePicture(Long id, MultipartFile file) {
    	TreeTypeEntity entity = treeTypeRepository.findById(id)
    		    .orElseThrow(() -> new ForestPlusException(HttpStatus.NOT_FOUND,
    		        "No se encontró el tipo de árbol con id " + id) {});

        String imageUuid = UUID.randomUUID().toString();
        
        String imageUrl = fileStorageService.storeFile(file, "tree-types", imageUuid);
        entity.setPicture(imageUrl);

        treeTypeRepository.save(entity);
        return treeTypeMapper.toResponse(entity);
    }
}
