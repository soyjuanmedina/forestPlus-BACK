package com.forestplus.service;

import com.forestplus.dto.request.CompanyCompensationRequest;
import com.forestplus.dto.response.CompanyCompensationResponse;
import com.forestplus.entity.CompanyCompensationEntity;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.mapper.CompanyCompensationMapper;
import com.forestplus.repository.CompanyCompensationRepository;
import com.forestplus.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyCompensationServiceImpl implements CompanyCompensationService {

    private final CompanyCompensationRepository compensationRepository;
    private final CompanyRepository companyRepository;
    private final CompanyCompensationMapper mapper;

    @Override
    public CompanyCompensationResponse create(CompanyCompensationRequest request) {
        CompanyEntity company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Compañía no encontrada"));

        CompanyCompensationEntity compensation = CompanyCompensationEntity.builder()
                .company(company)
                .year(request.getYear())
                .totalCompensations(request.getTotalCompensations())
                .build();

        return mapper.toResponse(compensationRepository.save(compensation));
    }

    @Override
    public List<CompanyCompensationResponse> findAll() {
        return compensationRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public List<CompanyCompensationResponse> findByCompany(Long companyId) {
        return compensationRepository.findByCompanyId(companyId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        compensationRepository.deleteById(id);
    }
}
