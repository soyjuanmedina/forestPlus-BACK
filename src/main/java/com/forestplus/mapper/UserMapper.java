package com.forestplus.mapper;

import com.forestplus.entity.UserEntity;
import com.forestplus.request.RegisterUserByAdminRequest;
import com.forestplus.request.RegisterUserRequest;
import com.forestplus.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // De entidad a response
    UserResponse toResponse(UserEntity user);

    @Mapping(target = "passwordHash", expression = "java(passwordEncoder.encode(request.getPassword()))")
    UserEntity toEntity(RegisterUserRequest request, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder);
    
 // Para Admin: asigna la contraseña generada
    @Mapping(target = "passwordHash", expression = "java(passwordEncoder.encode(randomPassword))")
    UserEntity toEntityWithPassword(RegisterUserByAdminRequest request, String randomPassword, PasswordEncoder passwordEncoder);


}
