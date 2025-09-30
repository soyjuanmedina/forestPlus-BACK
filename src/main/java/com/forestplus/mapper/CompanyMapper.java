package com.forestplus.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.forestplus.dto.request.CompanyRequest;
import com.forestplus.dto.response.CompanyResponse;
import com.forestplus.dto.response.UserResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.UserEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompanyMapper {

    // --- ENTITY -> RESPONSE ---
    CompanyResponse toResponse(CompanyEntity company);
    List<CompanyResponse> toResponseList(List<CompanyEntity> companies);

    // --- REQUEST -> ENTITY ---
    CompanyEntity toEntity(CompanyRequest request);

    // --- NUEVO: asignar admin por id ---
    default UserEntity mapAdminIdToUserEntity(Long id) {
        if (id == null) return null;
        UserEntity user = new UserEntity();
        user.setId(id);
        return user;
    }
}



