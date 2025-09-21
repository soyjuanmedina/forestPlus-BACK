package com.forestplus.service;

import com.forestplus.entity.CompanyEntity;
import com.forestplus.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public List<CompanyEntity> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public Optional<CompanyEntity> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    @Override
    public CompanyEntity createCompany(CompanyEntity company) {
        return companyRepository.save(company);
    }

    @Override
    public CompanyEntity updateCompany(Long id, CompanyEntity company) {
        return companyRepository.findById(id)
                .map(existing -> {
                    existing.setName(company.getName());
                    existing.setAddress(company.getAddress());
                    existing.setAdmin(company.getAdmin());
                    return companyRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Company not found with id " + id));
    }

    @Override
    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }
}
