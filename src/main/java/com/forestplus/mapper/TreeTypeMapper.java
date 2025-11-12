package com.forestplus.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.forestplus.dto.request.TreeTypeRequest;
import com.forestplus.dto.request.TreeTypeUpdateRequest;
import com.forestplus.dto.response.TreeTypeResponse;
import com.forestplus.entity.TreeTypeEntity;

@Mapper(componentModel = "spring")
public interface TreeTypeMapper {

    TreeTypeMapper INSTANCE = Mappers.getMapper(TreeTypeMapper.class);

    // Convertir DTO de creación a entidad
    TreeTypeEntity toEntity(TreeTypeRequest request);

    // Convertir DTO de actualización a entidad (ignorando ID)
    @Mapping(target = "id", ignore = true)
    TreeTypeEntity toEntity(TreeTypeUpdateRequest request);

    // Convertir entidad a DTO de respuesta
    TreeTypeResponse toResponse(TreeTypeEntity entity);
}
