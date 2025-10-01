package com.forestplus.mapper;

import com.forestplus.dto.request.CompanyRequest;
import com.forestplus.dto.response.CompanyResponse;
import com.forestplus.dto.response.CompanySummaryResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserSummaryMapper.class, UserMapper.class})
public interface CompanyMapper {

    @Mapping(target = "admin", source = "admin") // usa UserMapper → UserResponse
    CompanyResponse toResponse(CompanyEntity company);

    List<CompanyResponse> toResponseList(List<CompanyEntity> companies);

    CompanySummaryResponse toCompanySummary(CompanyEntity company);

    @Mapping(target = "admin", source = "adminId", qualifiedByName = "mapAdminIdToUserEntity")
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "lands", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    CompanyEntity toEntity(CompanyRequest request);

    @Named("mapAdminIdToUserEntity")
    default UserEntity mapAdminIdToUserEntity(Long id) {
        if (id == null) return null;
        UserEntity user = new UserEntity();
        user.setId(id);
        return user;
    }
}


