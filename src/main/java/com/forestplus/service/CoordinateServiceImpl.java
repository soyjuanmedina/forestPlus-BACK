package com.forestplus.service;

import com.forestplus.entity.CoordinateEntity;
import com.forestplus.repository.CoordinateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoordinateServiceImpl implements CoordinateService {

    private final CoordinateRepository coordinateRepository;

    public CoordinateServiceImpl(CoordinateRepository coordinateRepository) {
        this.coordinateRepository = coordinateRepository;
    }

    @Override
    public List<CoordinateEntity> getAllCoordinates() {
        return coordinateRepository.findAll();
    }

    @Override
    public Optional<CoordinateEntity> getCoordinateById(Long id) {
        return coordinateRepository.findById(id);
    }

    @Override
    public CoordinateEntity createCoordinate(CoordinateEntity coordinate) {
        return coordinateRepository.save(coordinate);
    }

    @Override
    public CoordinateEntity updateCoordinate(Long id, CoordinateEntity coordinate) {
        return coordinateRepository.findById(id)
                .map(existing -> {
                    existing.setLatitude(coordinate.getLatitude());
                    existing.setLongitude(coordinate.getLongitude());
                    existing.setLand(coordinate.getLand());
                    return coordinateRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Coordinate not found with id " + id));
    }

    @Override
    public void deleteCoordinate(Long id) {
        coordinateRepository.deleteById(id);
    }
}
