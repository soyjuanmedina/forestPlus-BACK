package com.forestplus.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.forestplus.dto.response.CompanyCO2YearlyResponse;
import com.forestplus.entity.CompanyCO2YearlyEntity;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.mapper.CompanyMapper;
import com.forestplus.repository.CompanyCO2YearlyRepository;
import com.forestplus.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyCO2YearlyService {

    private final CompanyRepository companyRepository;
    private final CompanyCO2YearlyRepository co2Repository;
    private final CompanyMapper companyMapper;

    public List<CompanyCO2YearlyResponse> getAllForCompany(Long companyId) {
        return co2Repository.findByCompanyId(companyId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CompanyCO2YearlyResponse createOrUpdate(Long companyId, int year, BigDecimal totalEmissions, BigDecimal totalCompensations) {
        CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        CompanyCO2YearlyEntity entity = co2Repository.findByCompanyIdAndYear(companyId, year)
                .orElse(new CompanyCO2YearlyEntity());

        entity.setCompany(company);
        entity.setYear(year);
        entity.setTotalEmissions(totalEmissions);
        entity.setTotalCompensations(totalCompensations);

        co2Repository.save(entity);
        return mapToDTO(entity);
    }

    public void delete(Long id) {
        if (!co2Repository.existsById(id)) throw new RuntimeException("CO2 record not found");
        co2Repository.deleteById(id);
    }

    private CompanyCO2YearlyResponse mapToDTO(CompanyCO2YearlyEntity entity) {
        return new CompanyCO2YearlyResponse(entity.getId(), entity.getYear(), entity.getTotalEmissions(), entity.getTotalCompensations());
    }
}
