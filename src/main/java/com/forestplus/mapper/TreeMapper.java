package com.forestplus.mapper;

import com.forestplus.dto.request.TreeRequest;
import com.forestplus.dto.request.TreeUpdateRequest;
import com.forestplus.dto.response.TreeResponse;
import com.forestplus.entity.TreeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TreeMapper {

    // --- ENTITY -> RESPONSE ---
    @Mapping(target = "treeTypeId", source = "treeType.id")
    @Mapping(target = "treeTypeName", source = "treeType.name")
    @Mapping(target = "treeType", source = "treeType")
    @Mapping(target = "landId", source = "land.id")
    @Mapping(target = "landName", source = "land.name")
    @Mapping(target = "land", source = "land")
    @Mapping(target = "ownerUserId", source = "ownerUser.id")
    @Mapping(target = "ownerUserName", source = "ownerUser.name")
    @Mapping(target = "ownerCompanyId", source = "ownerCompany.id")
    @Mapping(target = "ownerCompanyName", source = "ownerCompany.name")
    TreeResponse toResponse(TreeEntity tree);

    List<TreeResponse> toResponseList(List<TreeEntity> trees);

    // --- REQUEST -> ENTITY (sin ID) ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "treeType", ignore = true) // se setea en el service
    @Mapping(target = "land", ignore = true)     // se setea en el service
    @Mapping(target = "ownerUser", ignore = true)
    @Mapping(target = "ownerCompany", ignore = true)
    TreeEntity toEntity(TreeRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "treeType", ignore = true)
    @Mapping(target = "land", ignore = true)
    @Mapping(target = "ownerUser", ignore = true)
    @Mapping(target = "ownerCompany", ignore = true)
    TreeEntity toEntity(TreeUpdateRequest request);
}
