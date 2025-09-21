package com.forestplus.service;

import com.forestplus.entity.CompanyEntity;
import com.forestplus.mapper.CompanyMapper;
import com.forestplus.repository.CompanyRepository;
import com.forestplus.request.CompanyRequest;
import com.forestplus.response.CompanyResponse;
import com.forestplus.service.CompanyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyServiceImpl(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    @Override
    public List<CompanyResponse> getAllCompanies() {
        List<CompanyEntity> entities = companyRepository.findAll();
        return entities.stream()
                .map(companyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyResponse getCompanyById(Long id) {
        CompanyEntity entity = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found with id " + id));
        return companyMapper.toResponse(entity);
    }

    @Override
    public CompanyResponse createCompany(CompanyRequest request) {
        CompanyEntity entity = companyMapper.toEntity(request);
        CompanyEntity saved = companyRepository.save(entity);
        return companyMapper.toResponse(saved);
    }

    @Override
    public CompanyResponse updateCompany(Long id, CompanyRequest request) {
        CompanyEntity updated = companyRepository.findById(id).map(company -> {
            company.setName(request.getName());
            company.setAddress(request.getAddress());

            if (request.getAdminId() != null) {
                company.setAdmin(companyMapper.mapAdminIdToUserEntity(request.getAdminId()));
            }

            return companyRepository.save(company);
        }).orElseThrow(() -> new RuntimeException("Company not found with id " + id));

        return companyMapper.toResponse(updated);
    }

    @Override
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new RuntimeException("Company not found with id " + id);
        }
        companyRepository.deleteById(id);
    }
}
