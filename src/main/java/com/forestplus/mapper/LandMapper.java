package com.forestplus.mapper;

import com.forestplus.dto.request.LandRequest;
import com.forestplus.dto.response.LandResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.LandEntity;
import com.forestplus.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CompanyMapper.class})
public interface LandMapper {

    // --- ENTITY -> RESPONSE ---
    @Mapping(target = "users", source = "users")
    @Mapping(target = "companies", source = "companies")
    LandResponse toResponse(LandEntity land);

    List<LandResponse> toResponseList(List<LandEntity> lands);

    // --- REQUEST -> ENTITY ---
    @Mapping(target = "users", source = "userIds", qualifiedByName = "mapUserIdsToEntities")
    @Mapping(target = "companies", source = "companyIds", qualifiedByName = "mapCompanyIdsToEntities")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "coordinates", ignore = true) // se manejará aparte
    LandEntity toEntity(LandRequest request);

    // --- Métodos auxiliares ---
    @Named("mapUserIdsToEntities")
    default List<UserEntity> mapUserIdsToEntities(List<Long> ids) {
        if (ids == null) return Collections.emptyList();
        return ids.stream().map(id -> {
            UserEntity user = new UserEntity();
            user.setId(id);
            return user;
        }).toList();
    }

    @Named("mapCompanyIdsToEntities")
    default List<CompanyEntity> mapCompanyIdsToEntities(List<Long> ids) {
        if (ids == null) return Collections.emptyList();
        return ids.stream().map(id -> {
            CompanyEntity company = new CompanyEntity();
            company.setId(id);
            return company;
        }).toList();
    }
}
