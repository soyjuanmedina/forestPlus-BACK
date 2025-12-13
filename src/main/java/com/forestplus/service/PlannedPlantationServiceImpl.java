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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlannedPlantationServiceImpl implements PlannedPlantationService {

    private final PlannedPlantationRepository plannedPlantationRepository;
    private final LandRepository landRepository;
    private final PlannedPlantationMapper mapper;

    @Override
    public List<PlannedPlantationResponse> getAllPlannedPlantations() {
        return plannedPlantationRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PlannedPlantationResponse getPlannedPlantationById(Long id) {
        return plannedPlantationRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Planned Plantation not found with id " + id));
    }

    @Override
    @Transactional
    public PlannedPlantationResponse createPlannedPlantation(PlannedPlantationRequest request) {
        PlannedPlantationEntity entity = mapper.toEntity(request);

        if (request.getLandId() != null) {
            LandEntity land = landRepository.findById(request.getLandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Land not found with id " + request.getLandId()));
            entity.setLand(land);
        }

        return mapper.toResponse(plannedPlantationRepository.save(entity));
    }

    @Override
    @Transactional
    public PlannedPlantationResponse updatePlannedPlantation(Long id, PlannedPlantationUpdateRequest request) {
        PlannedPlantationEntity entity = plannedPlantationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planned Plantation not found with id " + id));

        mapper.updateEntityFromDto(request, entity);

        if (request.getLandId() == null) {
            entity.setLand(null); // desasignar
        } else {
            LandEntity land = landRepository.findById(request.getLandId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Land not found with id " + request.getLandId()
                ));
            entity.setLand(land);
        }

        return mapper.toResponse(plannedPlantationRepository.save(entity));
    }

    @Override
    public void deletePlannedPlantation(Long id) {
        plannedPlantationRepository.deleteById(id);
    }

    @Override
    public List<PlannedPlantationResponse> getPlannedPlantationsByLand(Long landId) {
        return plannedPlantationRepository.findByLandId(landId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlannedPlantationResponse> getPlannedPlantationsWithoutLand() {
        return plannedPlantationRepository.findByLandIsNull()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlannedPlantationResponse> getPlannedPlantationsBetweenDates(LocalDate start, LocalDate end) {
        return plannedPlantationRepository.findByPlannedDateBetween(start, end)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlannedPlantationResponse> getExecutedPlannedPlantations() {
        return plannedPlantationRepository.findByEffectiveDateIsNotNull()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlannedPlantationResponse> getPendingPlannedPlantations() {
        return plannedPlantationRepository.findByEffectiveDateIsNull()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}
