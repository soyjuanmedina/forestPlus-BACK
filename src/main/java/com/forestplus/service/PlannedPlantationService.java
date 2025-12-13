package com.forestplus.service;

import com.forestplus.dto.request.PlannedPlantationRequest;
import com.forestplus.dto.request.PlannedPlantationUpdateRequest;
import com.forestplus.dto.response.PlannedPlantationResponse;

import java.time.LocalDate;
import java.util.List;

public interface PlannedPlantationService {

    List<PlannedPlantationResponse> getAllPlannedPlantations();

    PlannedPlantationResponse getPlannedPlantationById(Long id);

    PlannedPlantationResponse createPlannedPlantation(PlannedPlantationRequest request);

    PlannedPlantationResponse updatePlannedPlantation(Long id, PlannedPlantationUpdateRequest request);

    void deletePlannedPlantation(Long id);

    List<PlannedPlantationResponse> getPlannedPlantationsByLand(Long landId);

    List<PlannedPlantationResponse> getPlannedPlantationsWithoutLand();

    List<PlannedPlantationResponse> getPlannedPlantationsBetweenDates(LocalDate start, LocalDate end);

    List<PlannedPlantationResponse> getExecutedPlannedPlantations();

    List<PlannedPlantationResponse> getPendingPlannedPlantations();
}
