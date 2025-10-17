package com.forestplus.service;

import com.forestplus.dto.request.CompanyRequest;
import com.forestplus.dto.response.CompanyResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.UserEntity;
import com.forestplus.exception.CompanyNotFoundException;
import com.forestplus.mapper.CompanyMapper;
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
    private final UserRepository userRepository; // Para asociar admin si corresponde
    private final CompanyMapper companyMapper;
    private final FileStorageService fileStorageService;

    @Override
    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll()
                .stream()
                .map(companyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyResponse getCompanyById(Long id) {
        CompanyEntity entity = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));
        return companyMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public CompanyResponse createCompany(CompanyRequest request) {
        CompanyEntity entity = companyMapper.toEntity(request);

        // Si viene un adminId, lo buscamos y lo asociamos
        if (request.getAdminId() != null) {
            UserEntity admin = userRepository.findById(request.getAdminId())
                    .orElseThrow(() -> new RuntimeException("Admin user not found with id " + request.getAdminId()));
            entity.setAdmin(admin);
        }

        CompanyEntity saved = companyRepository.save(entity);
        return companyMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CompanyResponse updateCompany(Long id, CompanyRequest request) {
        CompanyEntity updated = companyRepository.findById(id)
                .map(company -> {
                    company.setName(request.getName());
                    company.setAddress(request.getAddress());

                    if (request.getAdminId() != null) {
                        UserEntity admin = userRepository.findById(request.getAdminId())
                                .orElseThrow(() -> new RuntimeException("Admin user not found with id " + request.getAdminId()));
                        company.setAdmin(admin);
                    } else {
                        company.setAdmin(null);
                    }

                    return companyRepository.save(company);
                })
                .orElseThrow(() -> new CompanyNotFoundException(id));

        return companyMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new CompanyNotFoundException(id);
        }
        companyRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public CompanyResponse updateCompanyPicture(Long id, MultipartFile file) {
        // 1️⃣ Buscar la compañía
        CompanyEntity company = companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException(id));

        // 2️⃣ Guardar la imagen en el almacenamiento
        String imageUrl = fileStorageService.storeFile(file, "companies", company.getId());
        // Ejemplo: /uploads/companies/{id}-{filename}

        // 3️⃣ Actualizar la entidad con la nueva URL
        company.setPicture(imageUrl);

        // 4️⃣ Guardar cambios
        companyRepository.save(company);

        // 5️⃣ Devolver DTO actualizado
        return companyMapper.toResponse(company);
    }
}
