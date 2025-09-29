package com.forestplus.mapper;

import com.forestplus.dto.request.CompanyRequest;
import com.forestplus.dto.response.CompanyResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.UserEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CompanyMapper {

	@Mapping(source = "admin", target = "admin")
	@Mapping(source = "users", target = "users")
	CompanyResponse toResponse(CompanyEntity company);

    List<CompanyResponse> toResponseList(List<CompanyEntity> companies);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "admin", source = "adminId", qualifiedByName = "mapAdminIdToUserEntity")
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    CompanyEntity toEntity(CompanyRequest request);

    @Named("mapAdminIdToUserEntity")
    default UserEntity mapAdminIdToUserEntity(Long adminId) {
        if (adminId == null) return null;
        UserEntity user = new UserEntity();
        user.setId(adminId);
        return user;
    }
}
