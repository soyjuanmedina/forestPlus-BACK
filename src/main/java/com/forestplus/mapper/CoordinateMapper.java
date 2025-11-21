package com.forestplus.mapper;

import com.forestplus.dto.request.CoordinateRequest;
import com.forestplus.dto.request.CoordinateUpdateRequest;
import com.forestplus.dto.response.CoordinateResponse;
import com.forestplus.entity.CoordinateEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CoordinateMapper {

    // ---------- REQUEST → ENTITY ----------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "land", ignore = true) // se asigna en el servicio
    CoordinateEntity toEntity(CoordinateRequest request);


    // ---------- UPDATE → ENTITY ----------
    @Mapping(target = "land", ignore = true) // solo se actualiza lat/lng
    void updateEntity(@MappingTarget CoordinateEntity entity, CoordinateUpdateRequest request);


    // ---------- ENTITY → RESPONSE ----------
    @Mapping(target = "landId", source = "land.id")
    @Mapping(target = "landName", source = "land.name")
    CoordinateResponse toResponse(CoordinateEntity entity);
}
