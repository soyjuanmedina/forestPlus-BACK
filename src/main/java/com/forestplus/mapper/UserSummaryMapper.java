package com.forestplus.mapper;

import com.forestplus.dto.response.UserSummaryResponse;
import com.forestplus.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserSummaryMapper {

    // Convierte UserEntity -> UserSummaryResponse
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "surname", source = "surname")
    @Mapping(target = "email", source = "email")
    UserSummaryResponse toSummary(UserEntity user);

    // Lista
    List<UserSummaryResponse> toSummaryList(List<UserEntity> users);
}
