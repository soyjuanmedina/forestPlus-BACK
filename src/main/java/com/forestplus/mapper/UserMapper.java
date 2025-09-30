package com.forestplus.mapper;

import com.forestplus.dto.request.RegisterUserByAdminRequest;
import com.forestplus.dto.request.RegisterUserRequest;
import com.forestplus.dto.response.UserResponse;
import com.forestplus.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CompanyMapper.class})
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // --- ENTITY -> RESPONSE ---
    @Mapping(target = "company", source = "company")
    UserResponse toResponse(UserEntity user);

    List<UserResponse> toResponseList(List<UserEntity> users);

    // --- REQUEST -> ENTITY ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "forcePasswordChange", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lands", ignore = true)
    UserEntity toEntity(RegisterUserRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "forcePasswordChange", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lands", ignore = true)
    UserEntity toEntity(RegisterUserByAdminRequest request);
}
