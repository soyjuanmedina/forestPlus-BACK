package com.forestplus.service;

import com.forestplus.dto.request.CompanyEmissionRequest;
import com.forestplus.dto.response.CompanyEmissionResponse;

import java.util.List;

public interface CompanyEmissionService {

    CompanyEmissionResponse create(CompanyEmissionRequest request);

    List<CompanyEmissionResponse> findAll();

    List<CompanyEmissionResponse> findByCompany(Long companyId);

    void delete(Long id);
}
