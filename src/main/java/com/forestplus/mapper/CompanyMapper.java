package com.forestplus.mapper;

import com.forestplus.dto.request.CompanyRequest;
import com.forestplus.dto.response.CompanyResponse;
import com.forestplus.dto.response.CompanySummaryResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.UserEntity;
import org.mapstruct.*;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserSummaryMapper.class, UserMapper.class, CompanyEmissionMapper.class, CompanyCompensationMapper.class})
public abstract class CompanyMapper {

    @Autowired
    protected CompanyEmissionMapper emissionMapper;

    @Autowired
    protected CompanyCompensationMapper compensationMapper;

    // =======================
    // Mapeo de entidad a DTO
    // =======================
    @Mapping(target = "admin", source = "admin") // usa UserMapper
    @Mapping(target = "emissions", ignore = true)
    @Mapping(target = "compensations", ignore = true)
    public abstract CompanyResponse toResponse(CompanyEntity company);

    @AfterMapping
    protected void fillEmissionsAndCompensations(CompanyEntity company, @MappingTarget CompanyResponse dto) {
        dto.setEmissions(company.getEmissions() != null
                ? company.getEmissions().stream().map(emissionMapper::toResponse).toList()
                : List.of());

        dto.setCompensations(company.getCompensations() != null
                ? company.getCompensations().stream().map(compensationMapper::toResponse).toList()
                : List.of());
    }

    public abstract List<CompanyResponse> toResponseList(List<CompanyEntity> companies);

    public abstract CompanySummaryResponse toCompanySummary(CompanyEntity company);

    // =======================
    // Mapeo de DTO a entidad
    // =======================
    @Mapping(target = "admin", source = "adminId", qualifiedByName = "mapAdminIdToUserEntity")
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "lands", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "emissions", ignore = true)
    @Mapping(target = "compensations", ignore = true)
    public abstract CompanyEntity toEntity(CompanyRequest request);

    @Named("mapAdminIdToUserEntity")
    protected UserEntity mapAdminIdToUserEntity(Long id) {
        if (id == null) return null;
        UserEntity user = new UserEntity();
        user.setId(id);
        return user;
    }
}
