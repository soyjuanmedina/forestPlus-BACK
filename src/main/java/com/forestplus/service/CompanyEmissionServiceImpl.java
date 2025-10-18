package com.forestplus.service;

import com.forestplus.dto.request.CompanyEmissionRequest;
import com.forestplus.dto.response.CompanyEmissionResponse;
import com.forestplus.entity.CompanyEmissionEntity;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.mapper.CompanyEmissionMapper;
import com.forestplus.repository.CompanyEmissionRepository;
import com.forestplus.repository.CompanyRepository;
import com.forestplus.service.CompanyEmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyEmissionServiceImpl implements CompanyEmissionService {

    private final CompanyEmissionRepository emissionRepository;
    private final CompanyRepository companyRepository;
    private final CompanyEmissionMapper mapper;

    @Override
    public CompanyEmissionResponse create(CompanyEmissionRequest request) {
        CompanyEntity company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Compañía no encontrada"));

        CompanyEmissionEntity emission = CompanyEmissionEntity.builder()
                .company(company)
                .year(request.getYear())
                .totalEmissions(request.getTotalEmissions())
                .build();

        return mapper.toResponse(emissionRepository.save(emission));
    }

    @Override
    public List<CompanyEmissionResponse> findAll() {
        return emissionRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    public List<CompanyEmissionResponse> findByCompany(Long companyId) {
        return emissionRepository.findByCompanyId(companyId).stream().map(mapper::toResponse).toList();
    }

    @Override
    public void delete(Long id) {
        emissionRepository.deleteById(id);
    }
}
