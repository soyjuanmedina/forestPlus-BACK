package com.forestplus.service;

import com.forestplus.dto.request.CoordinateRequest;
import com.forestplus.dto.request.CoordinateUpdateRequest;
import com.forestplus.dto.response.CoordinateResponse;

import java.util.List;

public interface CoordinateService {

    // -------------------------------
    // CRUD básico
    // -------------------------------
    List<CoordinateResponse> getAllCoordinates();

    CoordinateResponse getCoordinateById(Long id);

    CoordinateResponse createCoordinate(CoordinateRequest request);

    CoordinateResponse updateCoordinate(Long id, CoordinateUpdateRequest request);

    void deleteCoordinate(Long id);

    // -------------------------------
    // Operaciones por parcela (land)
    // -------------------------------
    List<CoordinateResponse> getCoordinatesByLand(Long landId);

    void deleteCoordinatesByLand(Long landId);
}
