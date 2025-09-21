package com.forestplus.mapper;

import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.UserEntity;
import com.forestplus.request.CompanyRequest;
import com.forestplus.response.CompanyResponse;
import com.forestplus.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CompanyMapper {

    // Entity -> Response
    @Mapping(source = "admin", target = "admin")
    @Mapping(source = "users", target = "users")
    CompanyResponse toResponse(CompanyEntity company);

    List<CompanyResponse> toResponseList(List<CompanyEntity> companies);

    // Request -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "admin", source = "adminId", qualifiedByName = "mapAdminIdToUserEntity")
    @Mapping(target = "users", ignore = true) // Se gestionan en servicio
    @Mapping(target = "createdAt", ignore = true)
    CompanyEntity toEntity(CompanyRequest request);

    // Método auxiliar para convertir adminId -> UserEntity
    @Named("mapAdminIdToUserEntity")
    default UserEntity mapAdminIdToUserEntity(Long adminId) {
        if (adminId == null) return null;
        UserEntity user = new UserEntity();
        user.setId(adminId);
        return user;
    }
}
