package com.forestplus.service;

import com.forestplus.entity.CoordinateEntity;

import java.util.List;
import java.util.Optional;

public interface CoordinateService {
    List<CoordinateEntity> getAllCoordinates();
    Optional<CoordinateEntity> getCoordinateById(Long id);
    CoordinateEntity createCoordinate(CoordinateEntity coordinate);
    CoordinateEntity updateCoordinate(Long id, CoordinateEntity coordinate);
    void deleteCoordinate(Long id);
}
