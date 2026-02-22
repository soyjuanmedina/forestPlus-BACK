package com.forestplus.service;

import com.forestplus.dto.request.PlannedPlantationRequest;
import com.forestplus.dto.request.PlannedPlantationUpdateRequest;
import com.forestplus.dto.response.PlannedPlantationResponse;
import com.forestplus.entity.LandEntity;
import com.forestplus.entity.PlannedPlantationEntity;
import com.forestplus.exception.ResourceNotFoundException;
import com.forestplus.mapper.PlannedPlantationMapper;
import com.forestplus.repository.LandRepository;
import com.forestplus.repository.PlannedPlantationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlannedPlantationServiceImplTest {

    @Mock
    private PlannedPlantationRepository plannedPlantationRepository;

    @Mock
    private LandRepository landRepository;

    @Mock
    private PlannedPlantationMapper mapper;

    @InjectMocks
    private PlannedPlantationServiceImpl service;

    @Test
    void shouldGetAllPlannedPlantations() {
        PlannedPlantationEntity entity = new PlannedPlantationEntity();
        PlannedPlantationResponse response = new PlannedPlantationResponse();
        when(plannedPlantationRepository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        List<PlannedPlantationResponse> result = service.getAllPlannedPlantations();
        assertEquals(1, result.size());
        verify(plannedPlantationRepository).findAll();
        verify(mapper).toResponse(entity);
    }

    @Test
    void shouldGetPlannedPlantationById() {
        PlannedPlantationEntity entity = new PlannedPlantationEntity();
        PlannedPlantationResponse response = new PlannedPlantationResponse();
        when(plannedPlantationRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        PlannedPlantationResponse result = service.getPlannedPlantationById(1L);
        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenGettingNonExistingPlannedPlantation() {
        when(plannedPlantationRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getPlannedPlantationById(1L));
    }

    @Test
    void shouldCreatePlannedPlantationWithLand() {
        PlannedPlantationRequest request = new PlannedPlantationRequest();
        request.setLandId(10L);

        LandEntity land = new LandEntity();
        PlannedPlantationEntity entity = new PlannedPlantationEntity();
        PlannedPlantationResponse response = new PlannedPlantationResponse();

        when(mapper.toEntity(request)).thenReturn(entity);
        when(landRepository.findById(10L)).thenReturn(Optional.of(land));
        when(plannedPlantationRepository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        PlannedPlantationResponse result = service.createPlannedPlantation(request);
        assertNotNull(result);
        assertEquals(response, result);
        assertEquals(land, entity.getLand());
    }

    @Test
    void shouldThrowWhenCreatingPlannedPlantationWithInvalidLand() {
        PlannedPlantationRequest request = new PlannedPlantationRequest();
        request.setLandId(10L);
        PlannedPlantationEntity entity = new PlannedPlantationEntity();
        when(mapper.toEntity(request)).thenReturn(entity);
        when(landRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createPlannedPlantation(request));
    }

    @Test
    void shouldUpdatePlannedPlantationAndUnassignLand() {
        PlannedPlantationUpdateRequest request = new PlannedPlantationUpdateRequest();
        request.setLandId(null);

        PlannedPlantationEntity entity = mock(PlannedPlantationEntity.class);
        PlannedPlantationResponse response = new PlannedPlantationResponse();

        when(plannedPlantationRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(plannedPlantationRepository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        PlannedPlantationResponse result = service.updatePlannedPlantation(1L, request);

        assertNotNull(result);
        verify(entity).setLand(null);
        verify(mapper).updateEntityFromDto(request, entity);
    }

    @Test
    void shouldUpdatePlannedPlantationAndAssignLand() {
        PlannedPlantationUpdateRequest request = new PlannedPlantationUpdateRequest();
        request.setLandId(5L);

        PlannedPlantationEntity entity = new PlannedPlantationEntity();
        PlannedPlantationResponse response = new PlannedPlantationResponse();
        LandEntity land = new LandEntity();

        when(plannedPlantationRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(landRepository.findById(5L)).thenReturn(Optional.of(land));
        when(plannedPlantationRepository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(response);

        PlannedPlantationResponse result = service.updatePlannedPlantation(1L, request);

        assertNotNull(result);
        assertEquals(land, entity.getLand());
        verify(mapper).updateEntityFromDto(request, entity);
    }

    @Test
    void shouldDeletePlannedPlantation() {
        doNothing().when(plannedPlantationRepository).deleteById(1L);
        service.deletePlannedPlantation(1L);
        verify(plannedPlantationRepository).deleteById(1L);
    }

    @Test
    void shouldGetPlannedPlantationsByLand() {
        PlannedPlantationEntity entity = new PlannedPlantationEntity();
        PlannedPlantationResponse response = new PlannedPlantationResponse();

        when(plannedPlantationRepository.findByLandId(10L)).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        List<PlannedPlantationResponse> result = service.getPlannedPlantationsByLand(10L);
        assertEquals(1, result.size());
    }

    @Test
    void shouldGetPlannedPlantationsWithoutLand() {
        PlannedPlantationEntity entity = new PlannedPlantationEntity();
        PlannedPlantationResponse response = new PlannedPlantationResponse();

        when(plannedPlantationRepository.findByLandIsNull()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        List<PlannedPlantationResponse> result = service.getPlannedPlantationsWithoutLand();
        assertEquals(1, result.size());
    }

    @Test
    void shouldGetPlannedPlantationsBetweenDates() {
        PlannedPlantationEntity entity = new PlannedPlantationEntity();
        PlannedPlantationResponse response = new PlannedPlantationResponse();

        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(10);

        when(plannedPlantationRepository.findByPlannedDateBetween(start, end)).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        List<PlannedPlantationResponse> result = service.getPlannedPlantationsBetweenDates(start, end);
        assertEquals(1, result.size());
    }

    @Test
    void shouldGetExecutedPlannedPlantations() {
        PlannedPlantationEntity entity = new PlannedPlantationEntity();
        PlannedPlantationResponse response = new PlannedPlantationResponse();

        when(plannedPlantationRepository.findByEffectiveDateIsNotNull()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        List<PlannedPlantationResponse> result = service.getExecutedPlannedPlantations();
        assertEquals(1, result.size());
    }

    @Test
    void shouldGetPendingPlannedPlantations() {
        PlannedPlantationEntity entity = new PlannedPlantationEntity();
        PlannedPlantationResponse response = new PlannedPlantationResponse();

        when(plannedPlantationRepository.findByEffectiveDateIsNull()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        List<PlannedPlantationResponse> result = service.getPendingPlannedPlantations();
        assertEquals(1, result.size());
    }
}