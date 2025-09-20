package com.forestplus.mapper;
import com.forestplus.response.UserResponse;
import com.forestplus.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toDto(UserEntity user);
}