package com.forestplus.mapper;

import com.forestplus.dto.request.PlannedPlantationRequest;
import com.forestplus.dto.request.PlannedPlantationUpdateRequest;
import com.forestplus.dto.response.PlannedPlantationResponse;
import com.forestplus.entity.PlannedPlantationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TreeTypeMapper.class, LandMapper.class})
public interface PlannedPlantationMapper {

    // --- ENTITY -> RESPONSE ---
    @Mapping(target = "landId", source = "land.id")
    @Mapping(target = "landName", source = "land.name")
    @Mapping(target = "land", source = "land")
    @Mapping(target = "treeTypeId", source = "treeType.id")
    @Mapping(target = "treeTypeName", source = "treeType.name")
    @Mapping(target = "treeType", source = "treeType")
    PlannedPlantationResponse toResponse(PlannedPlantationEntity plantation);

    List<PlannedPlantationResponse> toResponseList(List<PlannedPlantationEntity> plantations);

    // --- REQUEST -> ENTITY (sin ID) ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "land", ignore = true)
    @Mapping(target = "treeType", ignore = true)
    PlannedPlantationEntity toEntity(PlannedPlantationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "land", ignore = true)
    @Mapping(target = "treeType", ignore = true)
    PlannedPlantationEntity toEntity(PlannedPlantationUpdateRequest request);

    // --- Update existing entity from UpdateRequest ---
    void updateEntityFromDto(PlannedPlantationUpdateRequest request, @MappingTarget PlannedPlantationEntity entity);
}
