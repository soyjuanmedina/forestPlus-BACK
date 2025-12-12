package com.forestplus.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.forestplus.dto.request.TreeTypeRequest;
import com.forestplus.dto.request.TreeTypeUpdateRequest;
import com.forestplus.dto.response.TreeTypeResponse;
import com.forestplus.entity.TreeTypeEntity;
import com.forestplus.exception.TreeTypeNotFoundException;
import com.forestplus.mapper.TreeTypeMapper;
import com.forestplus.repository.TreeTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TreeTypeServiceImpl implements TreeTypeService {

    private final TreeTypeRepository treeTypeRepository;
    private final TreeTypeMapper treeTypeMapper;
    private final FileStorageService fileStorageService; // Para manejo de imágenes

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
                .orElseThrow(() -> new TreeTypeNotFoundException(id));
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
            .orElseThrow(() -> new TreeTypeNotFoundException(id));

        // Mapper actualiza la entidad existente con los datos del DTO
        treeTypeMapper.updateEntityFromDto(request, entity);

        TreeTypeEntity updated = treeTypeRepository.save(entity);

        return treeTypeMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteTreeType(Long id) {
        if (!treeTypeRepository.existsById(id)) {
            throw new TreeTypeNotFoundException(id);
        }
        treeTypeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public TreeTypeResponse updateTreeTypePicture(Long id, MultipartFile file) {
        TreeTypeEntity entity = treeTypeRepository.findById(id)
                .orElseThrow(() -> new TreeTypeNotFoundException(id));

        String imageUrl = fileStorageService.storeFile(file, "tree-types", entity.getId());
        entity.setPicture(imageUrl);

        treeTypeRepository.save(entity);
        return treeTypeMapper.toResponse(entity);
    }
}
