package com.forestplus.service;

import com.forestplus.dto.request.CompanyRequest;
import com.forestplus.dto.response.CompanyResponse;
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
    CompanyResponse updateCompany(Long id, CompanyRequest request);

    // Eliminar compañía
    void deleteCompany(Long id);

    // 📸 Actualizar imagen de perfil de la compañía
    CompanyResponse updateCompanyPicture(Long id, MultipartFile file);
}
