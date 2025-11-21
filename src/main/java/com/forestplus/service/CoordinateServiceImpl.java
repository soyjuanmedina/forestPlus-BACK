package com.forestplus.service;

import com.forestplus.dto.request.CoordinateRequest;
import com.forestplus.dto.request.CoordinateUpdateRequest;
import com.forestplus.dto.response.CoordinateResponse;
import com.forestplus.entity.CoordinateEntity;
import com.forestplus.entity.LandEntity;
import com.forestplus.exception.ResourceNotFoundException;
import com.forestplus.mapper.CoordinateMapper;
import com.forestplus.repository.CoordinateRepository;
import com.forestplus.repository.LandRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoordinateServiceImpl implements CoordinateService {

    private final CoordinateRepository coordinateRepository;
    private final LandRepository landRepository;
    private final CoordinateMapper mapper;

    // ---------------------------------------------------------
    // LISTAR TODAS
    // ---------------------------------------------------------
    @Override
    public List<CoordinateResponse> getAllCoordinates() {
        return coordinateRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    // ---------------------------------------------------------
    // OBTENER POR ID
    // ---------------------------------------------------------
    @Override
    public CoordinateResponse getCoordinateById(Long id) {
        return coordinateRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinate not found with id " + id));
    }

    // ---------------------------------------------------------
    // CREAR
    // ---------------------------------------------------------
    @Override
    @Transactional
    public CoordinateResponse createCoordinate(CoordinateRequest request) {

        CoordinateEntity entity = mapper.toEntity(request);

        LandEntity land = landRepository.findById(request.getLandId())
                .orElseThrow(() -> new ResourceNotFoundException("Land not found with id " + request.getLandId()));

        entity.setLand(land);

        CoordinateEntity saved = coordinateRepository.save(entity);

        return mapper.toResponse(saved);
    }

    // ---------------------------------------------------------
    // ACTUALIZAR
    // ---------------------------------------------------------
    @Override
    @Transactional
    public CoordinateResponse updateCoordinate(Long id, CoordinateUpdateRequest request) {

        CoordinateEntity entity = coordinateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinate not found with id " + id));

        mapper.updateEntity(entity, request);

        CoordinateEntity updated = coordinateRepository.save(entity);

        return mapper.toResponse(updated);
    }

    // ---------------------------------------------------------
    // BORRAR POR ID
    // ---------------------------------------------------------
    @Override
    @Transactional
    public void deleteCoordinate(Long id) {
        if (!coordinateRepository.existsById(id)) {
            throw new ResourceNotFoundException("Coordinate not found with id " + id);
        }
        coordinateRepository.deleteById(id);
    }

    // ---------------------------------------------------------
    // LISTAR POR PARCELA (land_id)
    // ---------------------------------------------------------
    @Override
    public List<CoordinateResponse> getCoordinatesByLand(Long landId) {
        return coordinateRepository.findByLandId(landId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    // ---------------------------------------------------------
    // BORRAR TODAS LAS COORDENADAS DE UNA PARCELA
    // ---------------------------------------------------------
    @Override
    @Transactional
    public void deleteCoordinatesByLand(Long landId) {
        coordinateRepository.deleteByLandId(landId);
    }
}
