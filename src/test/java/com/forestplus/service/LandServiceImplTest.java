package com.forestplus.service;

import com.forestplus.dto.request.LandRequest;
import com.forestplus.dto.request.LandUpdateRequest;
import com.forestplus.dto.response.LandResponse;
import com.forestplus.entity.LandEntity;
import com.forestplus.mapper.LandMapper;
import com.forestplus.repository.CompanyRepository;
import com.forestplus.repository.LandRepository;
import com.forestplus.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LandServiceImplTest {

    @Mock
    private LandRepository landRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private LandMapper landMapper;

    @InjectMocks
    private LandServiceImpl landService;

    @Test
    void shouldCreateLand() {
        LandRequest request = new LandRequest();
        request.setName("Parcela 1");
        request.setLocation("Zona Norte");
        request.setArea(new BigDecimal("1000.0"));

        LandEntity entity = LandEntity.builder()
                .name(request.getName())
                .location(request.getLocation())
                .area(request.getArea())
                .build();

        LandResponse response = new LandResponse();
        response.setName(request.getName());

        when(landRepository.save(any(LandEntity.class))).thenReturn(entity);
        when(landMapper.toResponse(entity)).thenReturn(response);

        LandResponse result = landService.createLand(request);

        assertEquals("Parcela 1", result.getName());
        verify(landRepository).save(any(LandEntity.class));
        verify(landMapper).toResponse(entity);
    }

    @Test
    void shouldUpdateLand() {
        Long landId = 1L;
        LandUpdateRequest request = new LandUpdateRequest();
        request.setName("Parcela Actualizada");
        request.setDescription("Descripción");
        request.setLocation("Zona Sur");
        request.setArea(new BigDecimal("1200.0"));
        request.setPicture("img.png");
        request.setMaxTrees(100);

        LandEntity entity = new LandEntity();
        when(landRepository.findById(landId)).thenReturn(Optional.of(entity));
        when(landRepository.save(entity)).thenReturn(entity);
        LandResponse response = new LandResponse();
        response.setName(request.getName());
        when(landMapper.toResponse(entity)).thenReturn(response);

        LandResponse result = landService.updateLand(landId, request);

        assertEquals("Parcela Actualizada", result.getName());
        verify(landRepository).save(entity);
    }

    @Test
    void shouldThrowWhenUpdatingNonExistingLand() {
        Long landId = 1L;
        LandUpdateRequest request = new LandUpdateRequest();
        when(landRepository.findById(landId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> landService.updateLand(landId, request));

        assertEquals("Land not found", ex.getMessage());
    }

    @Test
    void shouldGetLandById() {
        Long landId = 1L;
        LandEntity entity = new LandEntity();
        when(landRepository.findById(landId)).thenReturn(Optional.of(entity));
        LandResponse response = new LandResponse();
        when(landMapper.toResponse(entity)).thenReturn(response);

        LandResponse result = landService.getLandById(landId);

        assertNotNull(result);
        verify(landMapper).toResponse(entity);
    }

    @Test
    void shouldThrowWhenGettingNonExistingLand() {
        Long landId = 1L;
        when(landRepository.findById(landId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> landService.getLandById(landId));

        assertEquals("Land not found", ex.getMessage());
    }

    @Test
    void shouldGetAllLands() {
        LandEntity entity1 = new LandEntity();
        LandEntity entity2 = new LandEntity();
        List<LandEntity> entities = List.of(entity1, entity2);

        when(landRepository.findAll()).thenReturn(entities);
        List<LandResponse> responseList = List.of(new LandResponse(), new LandResponse());
        when(landMapper.toResponseList(entities)).thenReturn(responseList);

        List<LandResponse> result = landService.getAllLands();

        assertEquals(2, result.size());
        verify(landRepository).findAll();
        verify(landMapper).toResponseList(entities);
    }

    @Test
    void shouldDeleteLand() {
        Long landId = 1L;
        doNothing().when(landRepository).deleteById(landId);

        landService.deleteLand(landId);

        verify(landRepository).deleteById(landId);
    }
}