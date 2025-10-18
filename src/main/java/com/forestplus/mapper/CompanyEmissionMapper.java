package com.forestplus.mapper;

import com.forestplus.dto.response.CompanyEmissionResponse;
import com.forestplus.entity.CompanyEmissionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CompanyEmissionMapper {

    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company.name", target = "companyName")
    @Mapping(target = "createdAt", expression = "java(emission.getCreatedAt().toString())")
    CompanyEmissionResponse toResponse(CompanyEmissionEntity emission);
}
