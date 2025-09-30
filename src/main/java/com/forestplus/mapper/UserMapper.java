package com.forestplus.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.forestplus.dto.request.RegisterUserByAdminRequest;
import com.forestplus.dto.request.RegisterUserRequest;
import com.forestplus.dto.response.UserResponse;
import com.forestplus.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(RegisterUserRequest dto);

    UserEntity toEntity(RegisterUserByAdminRequest dto); // <--- este es nuevo

    UserResponse toResponse(UserEntity entity);
}
