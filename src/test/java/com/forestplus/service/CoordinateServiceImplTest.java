package com.forestplus.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.forestplus.dto.request.CoordinateRequest;
import com.forestplus.dto.request.CoordinateUpdateRequest;
import com.forestplus.dto.response.CoordinateResponse;
import com.forestplus.entity.CoordinateEntity;
import com.forestplus.entity.LandEntity;
import com.forestplus.exception.ResourceNotFoundException;
import com.forestplus.mapper.CoordinateMapper;
import com.forestplus.repository.CoordinateRepository;
import com.forestplus.repository.LandRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CoordinateServiceImplTest {

    @Mock
    private CoordinateRepository coordinateRepository;

    @Mock
    private LandRepository landRepository;

    @Mock
    private CoordinateMapper mapper;

    @InjectMocks
    private CoordinateServiceImpl service;

    private CoordinateEntity entity;
    private LandEntity land;

    @BeforeEach
    void setup() {
        entity = new CoordinateEntity();
        entity.setId(1L);

        land = new LandEntity();
        land.setId(100L);
    }

    // ---------------------------------------
    // GET ALL
    // ---------------------------------------
    @Test
    void shouldReturnAllCoordinates() {
        when(coordinateRepository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(new CoordinateResponse());

        List<CoordinateResponse> result = service.getAllCoordinates();

        assertEquals(1, result.size());
        verify(coordinateRepository).findAll();
    }

    // ---------------------------------------
    // GET BY ID
    // ---------------------------------------
    @Test
    void shouldReturnCoordinateById() {
        when(coordinateRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(new CoordinateResponse());

        CoordinateResponse response = service.getCoordinateById(1L);

        assertNotNull(response);
    }

    @Test
    void shouldThrowWhenCoordinateNotFound() {
        when(coordinateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getCoordinateById(1L));
    }

    // ---------------------------------------
    // CREATE
    // ---------------------------------------
    @Test
    void shouldCreateCoordinate() {
        CoordinateRequest request = new CoordinateRequest();
        request.setLandId(100L);

        when(mapper.toEntity(request)).thenReturn(entity);
        when(landRepository.findById(100L)).thenReturn(Optional.of(land));
        when(coordinateRepository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(new CoordinateResponse());

        CoordinateResponse response = service.createCoordinate(request);

        assertNotNull(response);
        assertEquals(land, entity.getLand());
        verify(coordinateRepository).save(entity);
    }

    @Test
    void shouldThrowWhenLandNotFoundOnCreate() {
        CoordinateRequest request = new CoordinateRequest();
        request.setLandId(100L);

        when(mapper.toEntity(request)).thenReturn(entity);
        when(landRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.createCoordinate(request));
    }

    // ---------------------------------------
    // UPDATE
    // ---------------------------------------
    @Test
    void shouldUpdateCoordinate() {
        CoordinateUpdateRequest request = new CoordinateUpdateRequest();

        when(coordinateRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(coordinateRepository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(new CoordinateResponse());

        CoordinateResponse response = service.updateCoordinate(1L, request);

        assertNotNull(response);
        verify(mapper).updateEntity(entity, request);
        verify(coordinateRepository).save(entity);
    }

    @Test
    void shouldThrowWhenUpdatingNonExistingCoordinate() {
        when(coordinateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateCoordinate(1L, new CoordinateUpdateRequest()));
    }

    // ---------------------------------------
    // DELETE
    // ---------------------------------------
    @Test
    void shouldDeleteCoordinate() {
        when(coordinateRepository.existsById(1L)).thenReturn(true);

        service.deleteCoordinate(1L);

        verify(coordinateRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistingCoordinate() {
        when(coordinateRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> service.deleteCoordinate(1L));
    }

    // ---------------------------------------
    // GET BY LAND
    // ---------------------------------------
    @Test
    void shouldReturnCoordinatesByLand() {
        when(coordinateRepository.findByLandId(100L)).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(new CoordinateResponse());

        List<CoordinateResponse> result = service.getCoordinatesByLand(100L);

        assertEquals(1, result.size());
        verify(coordinateRepository).findByLandId(100L);
    }

    // ---------------------------------------
    // DELETE BY LAND
    // ---------------------------------------
    @Test
    void shouldDeleteCoordinatesByLand() {
        service.deleteCoordinatesByLand(100L);

        verify(coordinateRepository).deleteByLandId(100L);
    }
}