package com.forestplus.mapper;

import com.forestplus.dto.request.LandRequest;
import com.forestplus.dto.request.LandUpdateRequest;
import com.forestplus.dto.response.LandResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.LandEntity;
import com.forestplus.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LandMapper {

    // --- ENTITY -> RESPONSE ---
    @Mapping(target = "userIds", source = "users")
    @Mapping(target = "companyIds", source = "companies")
    LandResponse toResponse(LandEntity land);

    List<LandResponse> toResponseList(List<LandEntity> lands);

    // --- REQUEST -> ENTITY ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "companies", ignore = true)
    @Mapping(target = "coordinates", ignore = true)
    LandEntity toEntity(LandRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "companies", ignore = true)
    @Mapping(target = "coordinates", ignore = true)
    LandEntity toEntity(LandUpdateRequest request);

    // --- Helpers para convertir listas de entidades a ids ---
    default List<Long> mapUsersToIds(List<UserEntity> users) {
        return users != null ? users.stream().map(UserEntity::getId).toList() : List.of();
    }

    default List<Long> mapCompaniesToIds(List<CompanyEntity> companies) {
        return companies != null ? companies.stream().map(CompanyEntity::getId).toList() : List.of();
    }
}
