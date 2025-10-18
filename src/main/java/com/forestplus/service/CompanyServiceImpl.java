package com.forestplus.service;

import com.forestplus.dto.request.CompanyRequest;
import com.forestplus.dto.request.CompanyUpdateRequest;
import com.forestplus.dto.response.CompanyCompensationResponse;
import com.forestplus.dto.response.CompanyEmissionResponse;
import com.forestplus.dto.response.CompanyResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.UserEntity;
import com.forestplus.exception.CompanyNotFoundException;
import com.forestplus.mapper.CompanyCompensationMapper;
import com.forestplus.mapper.CompanyEmissionMapper;
import com.forestplus.mapper.CompanyMapper;
import com.forestplus.model.RolesEnum;
import com.forestplus.repository.CompanyCompensationRepository;
import com.forestplus.repository.CompanyEmissionRepository;
import com.forestplus.repository.CompanyRepository;
import com.forestplus.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final CompanyMapper companyMapper;
    private final CompanyEmissionRepository emissionRepository;
    private final CompanyEmissionMapper emissionMapper;
    private final CompanyCompensationRepository compensationRepository;
    private final CompanyCompensationMapper compensationMapper;
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

        if (request.getAdminId() != null) {
            UserEntity admin = userRepository.findById(request.getAdminId())
                    .orElseThrow(() -> new RuntimeException("Admin user not found with id " + request.getAdminId()));
            entity.setAdmin(admin);
        }

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
        companyRepository.save(company);

        return mapCompanyWithEmissionsAndCompensations(company);
    }

    /**
     * Método auxiliar para mapear una compañía incluyendo sus emisiones y compensaciones.
     */
    private CompanyResponse mapCompanyWithEmissionsAndCompensations(CompanyEntity company) {
        CompanyResponse response = companyMapper.toResponse(company);

        List<CompanyEmissionResponse> emissions = emissionRepository.findByCompanyId(company.getId())
                .stream()
                .map(emissionMapper::toResponse)
                .collect(Collectors.toList());
        response.setEmissions(emissions);

        List<CompanyCompensationResponse> compensations = compensationRepository.findByCompanyId(company.getId())
                .stream()
                .map(compensationMapper::toResponse)
                .collect(Collectors.toList());
        response.setCompensations(compensations);

        return response;
    }
}
