package com.forestplus.mapper;

import com.forestplus.dto.request.LandRequest;
import com.forestplus.dto.request.LandUpdateRequest;
import com.forestplus.dto.response.LandResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.LandEntity;
import com.forestplus.entity.UserEntity;
import com.forestplus.entity.CoordinateEntity;
import com.forestplus.dto.response.CoordinateResponse;
import com.forestplus.mapper.CoordinateMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { CoordinateMapper.class })
public interface LandMapper {

    // --- ENTITY -> RESPONSE ---
    @Mapping(target = "userIds", source = "users")
    @Mapping(target = "companyIds", source = "companies")
    @Mapping(target = "coordinates", source = "coordinates")
    LandResponse toResponse(LandEntity land);

    List<LandResponse> toResponseList(List<LandEntity> lands);

    List<CoordinateResponse> toCoordinateResponseList(List<CoordinateEntity> coordinates);

    // --- REQUEST -> ENTITY ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "companies", ignore = true)
    LandEntity toEntity(LandRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "companies", ignore = true)
    LandEntity toEntity(LandUpdateRequest request);

    // --- Helpers para convertir listas de entidades a ids ---
    default List<Long> mapUsersToIds(List<UserEntity> users) {
        return users != null ? users.stream().map(UserEntity::getId).toList() : List.of();
    }

    default List<Long> mapCompaniesToIds(List<CompanyEntity> companies) {
        return companies != null ? companies.stream().map(CompanyEntity::getId).toList() : List.of();
    }
}
