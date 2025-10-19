package com.forestplus.service;

import com.forestplus.dto.request.CompanyRequest;
import com.forestplus.dto.request.CompanyUpdateRequest;
import com.forestplus.dto.response.CompanyResponse;
import com.forestplus.entity.UserEntity;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CompanyService {

    // Obtener todas las compañías
    List<CompanyResponse> getAllCompanies();

    // Obtener compañía por ID
    CompanyResponse getCompanyById(Long id);

    // Crear compañía
    CompanyResponse createCompany(CompanyRequest request);

    // Actualizar compañía
    CompanyResponse updateCompany(Long id, CompanyUpdateRequest request, UserEntity loggedUser);

    // Eliminar compañía
    void deleteCompany(Long id);

    // 📸 Actualizar imagen de perfil de la compañía
    CompanyResponse updateCompanyPicture(Long id, MultipartFile file);

}
