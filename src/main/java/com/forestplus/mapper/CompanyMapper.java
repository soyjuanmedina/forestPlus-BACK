package com.forestplus.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.forestplus.dto.request.CompanyRequest;
import com.forestplus.dto.response.CompanyResponse;
import com.forestplus.dto.response.CompanySummaryResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.UserEntity;

@Mapper(componentModel = "spring", uses = {UserSummaryMapper.class, UserMapper.class})
public abstract class CompanyMapper {

    // =======================
    // ENTITY -> DTO
    // =======================
    @Mapping(target = "admin", source = "admin") // usa UserMapper
    public abstract CompanyResponse toResponse(CompanyEntity company);

    public abstract List<CompanyResponse> toResponseList(List<CompanyEntity> companies);

    public abstract CompanySummaryResponse toCompanySummary(CompanyEntity company);

    // =======================
    // DTO -> ENTITY
    // =======================
    @Mapping(target = "admin", source = "adminId", qualifiedByName = "mapAdminIdToUserEntity")
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "lands", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract CompanyEntity toEntity(CompanyRequest request);

    @Named("mapAdminIdToUserEntity")
    protected UserEntity mapAdminIdToUserEntity(Long id) {
        if (id == null) return null;
        UserEntity user = new UserEntity();
        user.setId(id);
        return user;
    }
}
