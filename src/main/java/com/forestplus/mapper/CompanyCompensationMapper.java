package com.forestplus.mapper;

import com.forestplus.dto.response.CompanyCompensationResponse;
import com.forestplus.entity.CompanyCompensationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyCompensationMapper {

    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    @Mapping(target = "createdAt", expression = "java(compensation.getCreatedAt().toString())")
    CompanyCompensationResponse toResponse(CompanyCompensationEntity compensation);
}
