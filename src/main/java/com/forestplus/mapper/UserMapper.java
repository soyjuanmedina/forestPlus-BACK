package com.forestplus.mapper;

import com.forestplus.entity.UserEntity;
import com.forestplus.request.RegisterUserByAdminRequest;
import com.forestplus.request.RegisterUserRequest;
import com.forestplus.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(UserEntity user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "emailVerified", ignore = true) 
    @Mapping(target = "forcePasswordChange", ignore = true)
    UserEntity toEntity(RegisterUserRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "forcePasswordChange", ignore = true)
    UserEntity toEntity(RegisterUserByAdminRequest request);
}
