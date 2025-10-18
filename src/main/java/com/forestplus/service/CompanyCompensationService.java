package com.forestplus.service;

import com.forestplus.dto.request.CompanyCompensationRequest;
import com.forestplus.dto.response.CompanyCompensationResponse;

import java.util.List;

public interface CompanyCompensationService {

    CompanyCompensationResponse create(CompanyCompensationRequest request);

    List<CompanyCompensationResponse> findAll();

    List<CompanyCompensationResponse> findByCompany(Long companyId);

    void delete(Long id);
}
