package com.forestplus.mapper;

import com.forestplus.dto.request.CompanyRequest;
import com.forestplus.dto.response.CompanyResponse;
import com.forestplus.dto.response.UserResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CompanyMapper {

    // --- ENTITY -> RESPONSE ---
    @Mapping(target = "admin", source = "admin")
    @Mapping(target = "users", source = "users")
    CompanyResponse toResponse(CompanyEntity company);

    List<CompanyResponse> toResponseList(List<CompanyEntity> companies);

    // --- REQUEST -> ENTITY ---
    @Mapping(target = "admin", source = "adminId", qualifiedByName = "mapAdminIdToUserEntity")
    @Mapping(target = "users", ignore = true) // Se manejan aparte
    @Mapping(target = "lands", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    CompanyEntity toEntity(CompanyRequest request);

    // --- Mapea un adminId a UserEntity ---
    @Named("mapAdminIdToUserEntity")
    default UserEntity mapAdminIdToUserEntity(Long id) {
        if (id == null) return null;
        UserEntity user = new UserEntity();
        user.setId(id);
        return user;
    }
}
