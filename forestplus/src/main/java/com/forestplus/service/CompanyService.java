package com.forestplus.service;

import com.forestplus.entity.CompanyEntity;

import java.util.List;
import java.util.Optional;

public interface CompanyService {
    List<CompanyEntity> getAllCompanies();
    Optional<CompanyEntity> getCompanyById(Long id);
    CompanyEntity createCompany(CompanyEntity company);
    CompanyEntity updateCompany(Long id, CompanyEntity company);
    void deleteCompany(Long id);
}
