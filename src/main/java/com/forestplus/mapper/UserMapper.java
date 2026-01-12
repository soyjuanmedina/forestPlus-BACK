package com.forestplus.mapper;

import com.forestplus.dto.request.RegisterUserByAdminRequest;
import com.forestplus.dto.request.RegisterUserRequest;
import com.forestplus.dto.response.UserResponse;
import com.forestplus.dto.response.CompanySummaryResponse;
import com.forestplus.entity.UserEntity;
import com.forestplus.entity.CompanyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // --- ENTITY -> RESPONSE ---
    @Mapping(target = "company", source = "company")
    UserResponse toResponse(UserEntity user);

    List<UserResponse> toResponseList(List<UserEntity> users);

    // --- CompanyEntity -> CompanySummaryResponse ---
    CompanySummaryResponse toCompanySummary(CompanyEntity company);

    // --- REQUEST -> ENTITY ---
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "forcePasswordChange", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lands", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "loginCount", ignore = true)
    @Mapping(target = "loginErrorCount", ignore = true)
    @Mapping(target = "accountLocked", ignore = true)
    @Mapping(target = "receiveEmails", ignore = true)
    UserEntity toEntity(RegisterUserRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "forcePasswordChange", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lands", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "loginCount", ignore = true)
    @Mapping(target = "loginErrorCount", ignore = true)
    UserEntity toEntity(RegisterUserByAdminRequest request);
}
