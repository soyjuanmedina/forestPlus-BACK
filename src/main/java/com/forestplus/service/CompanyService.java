package com.forestplus.service;

import com.forestplus.request.CompanyRequest;
import com.forestplus.response.CompanyResponse;

import java.util.List;

public interface CompanyService {
    List<CompanyResponse> getAllCompanies();
    CompanyResponse getCompanyById(Long id);
    CompanyResponse createCompany(CompanyRequest request);
    CompanyResponse updateCompany(Long id, CompanyRequest request);
    void deleteCompany(Long id);
}
