package com.forestplus.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.forestplus.dto.request.TreeTypeRequest;
import com.forestplus.dto.request.TreeTypeUpdateRequest;
import com.forestplus.dto.response.TreeTypeResponse;

public interface TreeTypeService {

    List<TreeTypeResponse> getAllTreeTypes();

    TreeTypeResponse getTreeTypeById(Long id);

    TreeTypeResponse createTreeType(TreeTypeRequest request);

    TreeTypeResponse updateTreeType(Long id, TreeTypeUpdateRequest request);

    void deleteTreeType(Long id);

    TreeTypeResponse updateTreeTypePicture(Long id, MultipartFile file); // ← NUEVO
}
