package com.forestplus.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.forestplus.dto.request.CompanyRequest;
import com.forestplus.dto.response.CompanyResponse;
import com.forestplus.dto.response.CompanySummaryResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.UserEntity;

@Mapper(componentModel = "spring", uses = {UserSummaryMapper.class, UserMapper.class})
public abstract class CompanyMapper {

    // =======================
    // ENTITY -> DTO
    // =======================
    @Mapping(target = "admin", source = "admin") // usa UserMapper
    public abstract CompanyResponse toResponse(CompanyEntity company);

//    @AfterMapping
//    protected void fillCO2(CompanyEntity company, @MappingTarget CompanyResponse dto) {
//        Map<Integer, CompanyCO2YearDTO> summary = new HashMap<>();
//
////        // Emisiones
////        if (company.getEmissions() != null) {
////            company.getEmissions().forEach(e -> {
////                summary.computeIfAbsent(e.getYear(), y -> new CompanyCO2YearDTO());
////                CompanyCO2YearDTO yearDTO = summary.get(e.getYear());
////                if (yearDTO.getYear() == 0) yearDTO.setYear(e.getYear());
////
////                if (yearDTO.getEmissions() == null) yearDTO.setEmissions(new CompanyCO2ValueDTO());
////                BigDecimal current = yearDTO.getEmissions().getTotal() != null ? yearDTO.getEmissions().getTotal() : BigDecimal.ZERO;
////                yearDTO.getEmissions().setTotal(current.add(e.getTotalEmissions() != null ? e.getTotalEmissions() : BigDecimal.ZERO));
////            });
////        }
////
////        // Compensaciones
////        if (company.getCompensations() != null) {
////            company.getCompensations().forEach(c -> {
////                summary.computeIfAbsent(c.getYear(), y -> new CompanyCO2YearDTO());
////                CompanyCO2YearDTO yearDTO = summary.get(c.getYear());
////                if (yearDTO.getYear() == 0) yearDTO.setYear(c.getYear());
////
////                if (yearDTO.getCompensations() == null) yearDTO.setCompensations(new CompanyCO2ValueDTO());
////                BigDecimal current = yearDTO.getCompensations().getTotal() != null ? yearDTO.getCompensations().getTotal() : BigDecimal.ZERO;
////                yearDTO.getCompensations().setTotal(current.add(c.getTotalCompensations() != null ? c.getTotalCompensations() : BigDecimal.ZERO));
////            });
////        }
//
//        dto.setCo2(new ArrayList<>(summary.values())
//                .stream()
//                .sorted(Comparator.comparingInt(CompanyCO2YearDTO::getYear))
//                .toList());
//    }

    public abstract List<CompanyResponse> toResponseList(List<CompanyEntity> companies);

    public abstract CompanySummaryResponse toCompanySummary(CompanyEntity company);

    // =======================
    // DTO -> ENTITY
    // =======================
    @Mapping(target = "admin", source = "adminId", qualifiedByName = "mapAdminIdToUserEntity")
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "lands", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    public abstract CompanyEntity toEntity(CompanyRequest request);

    @Named("mapAdminIdToUserEntity")
    protected UserEntity mapAdminIdToUserEntity(Long id) {
        if (id == null) return null;
        UserEntity user = new UserEntity();
        user.setId(id);
        return user;
    }
}
