package com.forestplus.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.forestplus.dto.request.CompanyRequest;
import com.forestplus.dto.request.CompanyUpdateRequest;
import com.forestplus.dto.response.CompanyCO2YearlyResponse;
import com.forestplus.dto.response.CompanyResponse;
import com.forestplus.entity.CompanyCO2YearlyEntity;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.UserEntity;
import com.forestplus.exception.CompanyNotFoundException;
import com.forestplus.mapper.CompanyMapper;
import com.forestplus.model.RolesEnum;
import com.forestplus.repository.CompanyRepository;
import com.forestplus.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CompanyMapper companyMapper;
    private final FileStorageService fileStorageService;

    @Override
    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll()
                .stream()
                .map(this::mapCompanyWithEmissionsAndCompensations)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyResponse getCompanyById(Long id) {
        CompanyEntity entity = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));
        return mapCompanyWithEmissionsAndCompensations(entity);
    }

    @Transactional
    public CompanyResponse createCompany(CompanyRequest request) {
        CompanyEntity entity = companyMapper.toEntity(request);

        CompanyEntity saved = companyRepository.save(entity);
        return mapCompanyWithEmissionsAndCompensations(saved);
    }

    @Transactional
    public CompanyResponse updateCompany(Long id, CompanyUpdateRequest request, UserEntity loggedUser) {
        CompanyEntity updated = companyRepository.findById(id)
            .map(company -> {

                if (loggedUser.getRole() == RolesEnum.COMPANY_ADMIN 
                        && !company.getId().equals(loggedUser.getCompany().getId())) {
                    throw new RuntimeException("No tiene permiso para editar esta compañía");
                }

                company.setName(request.getName());
                company.setAddress(request.getAddress());

                return companyRepository.save(company);
            })
            .orElseThrow(() -> new CompanyNotFoundException(id));

        return mapCompanyWithEmissionsAndCompensations(updated);
    }

    @Transactional
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new CompanyNotFoundException(id);
        }
        companyRepository.deleteById(id);
    }

    @Transactional
    public CompanyResponse updateCompanyPicture(Long id, MultipartFile file) {
        CompanyEntity company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));

        String imageUrl = fileStorageService.storeFile(file, "companies", company.getId());
        company.setPicture(imageUrl);
        System.out.println("[#updateCompanyPicture] Imagen guardada en: " + imageUrl);
        companyRepository.save(company);

        return mapCompanyWithEmissionsAndCompensations(company);
    }

    /**
     * Método auxiliar para mapear una compañía incluyendo sus emisiones y compensaciones.
     */
    private CompanyResponse mapCompanyWithEmissionsAndCompensations(CompanyEntity company) {
        CompanyResponse response = companyMapper.toResponse(company);

        // 👉 Nuevo: convertir entidades CO2Yearly a DTO
        if (company.getCo2Yearly() != null) {
            List<CompanyCO2YearlyResponse> co2List = company.getCo2Yearly().stream()
                    .map(this::mapToCO2DTO)
                    .collect(Collectors.toList());
            response.setCo2(co2List);
        }

        return response;
    }

    private CompanyCO2YearlyResponse mapToCO2DTO(CompanyCO2YearlyEntity entity) {
        return new CompanyCO2YearlyResponse(
                entity.getId(),
                entity.getYear(),
                entity.getTotalEmissions(),
                entity.getTotalCompensations()
        );
    }
}
