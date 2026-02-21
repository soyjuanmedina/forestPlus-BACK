package com.forestplus.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.forestplus.dto.response.CompanyCO2YearlyResponse;
import com.forestplus.entity.CompanyCO2YearlyEntity;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.mapper.CompanyMapper;
import com.forestplus.repository.CompanyCO2YearlyRepository;
import com.forestplus.repository.CompanyRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompanyCO2YearlyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyCO2YearlyRepository co2Repository;

    @Mock
    private CompanyMapper companyMapper;

    @InjectMocks
    private CompanyCO2YearlyService service;

    private CompanyEntity company;

    @BeforeEach
    void setup() {
        company = new CompanyEntity();
        company.setId(1L);
    }

    @Test
    void shouldReturnAllCO2RecordsForCompany() {
        CompanyCO2YearlyEntity entity = new CompanyCO2YearlyEntity();
        entity.setId(1L);
        entity.setYear(2024);
        entity.setTotalEmissions(BigDecimal.TEN);
        entity.setTotalCompensations(BigDecimal.ONE);

        when(co2Repository.findByCompanyId(1L)).thenReturn(List.of(entity));

        List<CompanyCO2YearlyResponse> result = service.getAllForCompany(1L);

        assertEquals(1, result.size());
        assertEquals(2024, result.get(0).getYear());
        verify(co2Repository).findByCompanyId(1L);
    }

    @Test
    void shouldCreateNewCO2RecordIfNotExists() {
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(co2Repository.findByCompanyIdAndYear(1L, 2024))
                .thenReturn(Optional.empty());

        CompanyCO2YearlyResponse response = service.createOrUpdate(
                1L,
                2024,
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(20)
        );

        assertEquals(2024, response.getYear());
        assertEquals(BigDecimal.valueOf(100), response.getTotalEmissions());

        verify(co2Repository).save(any(CompanyCO2YearlyEntity.class));
    }

    @Test
    void shouldUpdateExistingCO2Record() {
        CompanyCO2YearlyEntity existing = new CompanyCO2YearlyEntity();
        existing.setId(5L);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(co2Repository.findByCompanyIdAndYear(1L, 2024))
                .thenReturn(Optional.of(existing));

        CompanyCO2YearlyResponse response = service.createOrUpdate(
                1L,
                2024,
                BigDecimal.valueOf(200),
                BigDecimal.valueOf(50)
        );

        assertEquals(2024, response.getYear());
        assertEquals(BigDecimal.valueOf(200), response.getTotalEmissions());
        verify(co2Repository).save(existing);
    }

    @Test
    void shouldThrowExceptionIfCompanyNotFound() {
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.createOrUpdate(1L, 2024, BigDecimal.TEN, BigDecimal.ONE)
        );

        assertEquals("Company not found", ex.getMessage());
    }

    @Test
    void shouldDeleteWhenExists() {
        when(co2Repository.existsById(10L)).thenReturn(true);

        service.delete(10L);

        verify(co2Repository).deleteById(10L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingRecord() {
        when(co2Repository.existsById(10L)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                service.delete(10L)
        );

        assertEquals("CO2 record not found", ex.getMessage());
    }
}