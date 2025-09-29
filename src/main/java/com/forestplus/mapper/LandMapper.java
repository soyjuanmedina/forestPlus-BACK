package com.forestplus.mapper;

import com.forestplus.dto.request.LandRequest;
import com.forestplus.dto.response.LandResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.LandEntity;
import com.forestplus.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface LandMapper {

	 // --- ENTITY -> RESPONSE ---
    @Mapping(source = "users", target = "users")
    @Mapping(source = "companies", target = "companies")
    LandResponse toResponse(LandEntity land);

    List<LandResponse> toResponseList(List<LandEntity> lands);

    // --- REQUEST -> ENTITY ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "coordinates", ignore = true)
    @Mapping(target = "users", ignore = true) // se llenará en el service
    @Mapping(target = "companies", source = "companyIds", qualifiedByName = "mapCompanyIdsToEntities")
    LandEntity toEntity(LandRequest request);

    // --- Helpers ---
    @Named("mapCompanyIdsToEntities")
    default List<CompanyEntity> mapCompanyIdsToEntities(List<Long> companyIds) {
        if (companyIds == null) return null;
        return companyIds.stream().map(id -> {
            CompanyEntity c = new CompanyEntity();
            c.setId(id);
            return c;
        }).toList();
    }

    @Named("mapCompaniesToIds")
    default List<Long> mapCompaniesToIds(List<CompanyEntity> companies) {
        if (companies == null) return null;
        return companies.stream().map(CompanyEntity::getId).toList();
    }

    @Named("mapUsersToIds")
    default List<Long> mapUsersToIds(List<UserEntity> users) {
        if (users == null) return null;
        return users.stream().map(UserEntity::getId).toList();
    }
}
