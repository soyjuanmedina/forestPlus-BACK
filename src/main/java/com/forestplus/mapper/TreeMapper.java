package com.forestplus.mapper;

import com.forestplus.dto.request.TreeBatchPlantRequest;
import com.forestplus.dto.request.TreeRequest;
import com.forestplus.dto.request.TreeUpdateRequest;
import com.forestplus.dto.response.TreeResponse;
import com.forestplus.entity.TreeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

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
    @Mapping(target = "treeType", ignore = true)
    @Mapping(target = "land", ignore = true)
    @Mapping(target = "ownerUser", ignore = true)
    @Mapping(target = "ownerCompany", ignore = true)
    TreeEntity toEntity(TreeRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "treeType", ignore = true)
    @Mapping(target = "land", ignore = true)
    @Mapping(target = "ownerUser", ignore = true)
    @Mapping(target = "ownerCompany", ignore = true)
    TreeEntity toEntity(TreeUpdateRequest request);

    // --- TreeBatchPlantRequest -> TreeEntity ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "treeType", ignore = true)
    @Mapping(target = "land", ignore = true)
    @Mapping(target = "ownerUser", ignore = true)
    @Mapping(target = "ownerCompany", ignore = true)
    @Mapping(target = "plantedAt", ignore = true) // lo seteas en el service
    @Mapping(target = "co2AbsorptionAt20", ignore = true) // lo seteas en el service
    @Mapping(target = "co2AbsorptionAt25", ignore = true)
    @Mapping(target = "co2AbsorptionAt30", ignore = true)
    @Mapping(target = "co2AbsorptionAt35", ignore = true)
    @Mapping(target = "co2AbsorptionAt40", ignore = true)
    TreeEntity toEntity(TreeBatchPlantRequest request);
    
    void updateEntityFromDto(TreeUpdateRequest request, @MappingTarget TreeEntity entity);
}
