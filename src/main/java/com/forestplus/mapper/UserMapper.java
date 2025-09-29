package com.forestplus.mapper;

import com.forestplus.dto.request.RegisterUserByAdminRequest;
import com.forestplus.dto.request.RegisterUserRequest;
import com.forestplus.dto.response.CompanyResponse;
import com.forestplus.dto.response.UserResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.UserEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // map UserEntity -> UserResponse
    @Mapping(target = "company", source = "company", qualifiedByName = "companyToResponse")
    UserResponse toResponse(UserEntity user);

    // map CompanyEntity -> CompanyResponse
    @Named("companyToResponse")
    default CompanyResponse companyToResponse(CompanyEntity company) {
        if (company == null) return null;
        return new CompanyResponse();
    }

    // map RegisterUserRequest -> UserEntity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "forcePasswordChange", ignore = true)
    @Mapping(target = "company", ignore = true) // se asigna manualmente en el servicio
    UserEntity toEntity(RegisterUserRequest request);

    // map RegisterUserByAdminRequest -> UserEntity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "forcePasswordChange", ignore = true)
    @Mapping(target = "company", ignore = true) // se asigna manualmente en el servicio
    UserEntity toEntity(RegisterUserByAdminRequest request);
}
