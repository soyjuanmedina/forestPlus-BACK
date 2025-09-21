package com.forestplus.mapper;
import com.forestplus.response.UserResponse;
import com.forestplus.dto.UserDto;
import com.forestplus.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toDto(UserEntity user);
    
    @Mapping(source = "password", target = "passwordHash")
    UserEntity toEntity(UserDto dto);
}