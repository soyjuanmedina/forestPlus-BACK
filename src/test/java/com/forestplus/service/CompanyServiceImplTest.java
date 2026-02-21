package com.forestplus.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.forestplus.dto.request.CompanyRequest;
import com.forestplus.dto.request.CompanyUpdateRequest;
import com.forestplus.dto.response.CompanyResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.UserEntity;
import com.forestplus.exception.CompanyNotFoundException;
import com.forestplus.mapper.CompanyMapper;
import com.forestplus.model.RolesEnum;
import com.forestplus.repository.CompanyRepository;
import com.forestplus.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private CompanyServiceImpl service;

    private CompanyEntity company;

    @BeforeEach
    void setup() {
        company = new CompanyEntity();
        company.setId(1L);
        company.setName("Test Company");
    }

    @Test
    void shouldReturnAllCompanies() {
        when(companyRepository.findAll()).thenReturn(List.of(company));
        when(companyMapper.toResponse(company)).thenReturn(new CompanyResponse());

        List<CompanyResponse> result = service.getAllCompanies();

        assertEquals(1, result.size());
        verify(companyRepository).findAll();
    }

    @Test
    void shouldReturnCompanyById() {
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(companyMapper.toResponse(company)).thenReturn(new CompanyResponse());

        CompanyResponse response = service.getCompanyById(1L);

        assertNotNull(response);
    }

    @Test
    void shouldThrowWhenCompanyNotFound() {
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class,
                () -> service.getCompanyById(1L));
    }

    @Test
    void shouldCreateCompany() {
        CompanyRequest request = new CompanyRequest();

        when(companyMapper.toEntity(request)).thenReturn(company);
        when(companyRepository.save(company)).thenReturn(company);
        when(companyMapper.toResponse(company)).thenReturn(new CompanyResponse());

        CompanyResponse response = service.createCompany(request);

        assertNotNull(response);
        verify(companyRepository).save(company);
    }

    @Test
    void shouldUpdateCompanyWhenAdminOfSameCompany() {
        CompanyUpdateRequest request = new CompanyUpdateRequest();
        request.setName("Updated");
        request.setAddress("Address");

        UserEntity user = new UserEntity();
        user.setRole(RolesEnum.COMPANY_ADMIN);
        user.setCompany(company);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(companyRepository.save(company)).thenReturn(company);
        when(companyMapper.toResponse(company)).thenReturn(new CompanyResponse());

        CompanyResponse response = service.updateCompany(1L, request, user);

        assertNotNull(response);
        assertEquals("Updated", company.getName());
    }

    @Test
    void shouldThrowWhenAdminEditingAnotherCompany() {
        CompanyEntity otherCompany = new CompanyEntity();
        otherCompany.setId(2L);

        UserEntity user = new UserEntity();
        user.setRole(RolesEnum.COMPANY_ADMIN);
        user.setCompany(otherCompany);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.updateCompany(1L, new CompanyUpdateRequest(), user));

        assertEquals("No tiene permiso para editar esta compañía", ex.getMessage());
    }

    @Test
    void shouldDeleteCompany() {
        when(companyRepository.existsById(1L)).thenReturn(true);

        service.deleteCompany(1L);

        verify(companyRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistingCompany() {
        when(companyRepository.existsById(1L)).thenReturn(false);

        assertThrows(CompanyNotFoundException.class,
                () -> service.deleteCompany(1L));
    }

    @Test
    void shouldUpdateCompanyPicture() {
        MultipartFile file = mock(MultipartFile.class);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(fileStorageService.storeFile(any(), eq("companies"), any()))
                .thenReturn("http://image.url");
        when(companyMapper.toResponse(company)).thenReturn(new CompanyResponse());

        CompanyResponse response = service.updateCompanyPicture(1L, file);

        assertNotNull(response);
        assertEquals("http://image.url", company.getPicture());
        verify(companyRepository).save(company);
    }
}