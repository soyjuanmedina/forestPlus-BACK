package com.forestplus.mapper;

import com.forestplus.entity.UserEntity;
import com.forestplus.request.RegisterUserByAdminRequest;
import com.forestplus.request.RegisterUserRequest;
import com.forestplus.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // De entidad a response
    UserResponse toResponse(UserEntity user);

    // Mapea de RegisterUserRequest a UserEntity (sin contraseña, se setea en el servicio)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true) // CORRECTO: se maneja en el servicio
    UserEntity toEntity(RegisterUserRequest request);

    // Para Admin: igual, el password se setea en el servicio
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true) 
    UserEntity toEntity(RegisterUserByAdminRequest request);
}
