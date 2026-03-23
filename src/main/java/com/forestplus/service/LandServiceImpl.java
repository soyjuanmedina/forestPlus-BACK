package com.forestplus.service;

import com.forestplus.dto.request.LandRequest;
import com.forestplus.dto.request.LandUpdateRequest;
import com.forestplus.dto.response.LandResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.LandEntity;
import com.forestplus.entity.UserEntity;
import com.forestplus.mapper.LandMapper;
import com.forestplus.repository.CompanyRepository;
import com.forestplus.repository.LandRepository;
import com.forestplus.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LandServiceImpl implements LandService {

    private final LandRepository landRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final LandMapper landMapper;
    private final com.forestplus.mapper.CoordinateMapper coordinateMapper;
    private final com.forestplus.repository.CoordinateRepository coordinateRepository;

    @Override
    @org.springframework.transaction.annotation.Transactional
    public LandResponse createLand(LandRequest request) {
        LandEntity land = landMapper.toEntity(request);
        if (land == null) {
            throw new RuntimeException("Error mapping land request");
        }
        
        if (land.getCoordinates() != null) {
            for (com.forestplus.entity.CoordinateEntity coord : land.getCoordinates()) {
                coord.setLand(land);
            }
        }
        
        land = landRepository.save(land);
        LandResponse response = landMapper.toResponse(land);
        
        // Refuerzo manual
        if (response.getCoordinates() == null || response.getCoordinates().isEmpty()) {
            List<com.forestplus.entity.CoordinateEntity> coords = coordinateRepository.findByLandId(response.getId());
            if (coords != null && !coords.isEmpty()) {
                response.setCoordinates(coords.stream().map(coordinateMapper::toResponse).toList());
            }
        }
        return response;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public LandResponse updateLand(Long id, LandUpdateRequest request) {
        LandEntity land = landRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Land not found"));

        land.setName(request.getName());
        land.setDescription(request.getDescription());
        land.setLocation(request.getLocation());
        land.setArea(request.getArea());
        land.setPicture(request.getPicture());
        land.setMaxTrees(request.getMaxTrees());

        if (request.getCoordinates() != null) {
            if (land.getCoordinates() == null) {
                land.setCoordinates(new java.util.ArrayList<>());
            } else {
                land.getCoordinates().clear();
            }
            
            for (com.forestplus.dto.request.CoordinateRequest coordReq : request.getCoordinates()) {
                com.forestplus.entity.CoordinateEntity ce = coordinateMapper.toEntity(coordReq);
                ce.setLand(land);
                land.getCoordinates().add(ce);
            }
        }

        land = landRepository.save(land);
        LandResponse response = landMapper.toResponse(land);
        
        // Refuerzo manual
        if (response.getCoordinates() == null || response.getCoordinates().isEmpty()) {
            if (response.getId() != null) {
                List<com.forestplus.entity.CoordinateEntity> coords = coordinateRepository.findByLandId(response.getId());
                if (coords != null && !coords.isEmpty()) {
                    response.setCoordinates(coords.stream().map(coordinateMapper::toResponse).toList());
                }
            }
        }
        return response;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public LandResponse getLandById(Long id) {
        LandEntity land = landRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Land not found"));
        LandResponse response = landMapper.toResponse(land);
        
        // Refuerzo manual por si el mapper tiene problemas con la carga diferida
        if (response.getCoordinates() == null || response.getCoordinates().isEmpty()) {
            List<com.forestplus.entity.CoordinateEntity> coords = coordinateRepository.findByLandId(id);
            if (coords != null && !coords.isEmpty()) {
                response.setCoordinates(coords.stream().map(coordinateMapper::toResponse).toList());
            }
        }
        return response;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<LandResponse> getAllLands() {
        List<LandEntity> lands = landRepository.findAll();
        List<LandResponse> responses = landMapper.toResponseList(lands);
        
        // Refuerzo para todos los terrenos
        for (LandResponse response : responses) {
            if (response.getCoordinates() == null || response.getCoordinates().isEmpty()) {
                List<com.forestplus.entity.CoordinateEntity> coords = coordinateRepository.findByLandId(response.getId());
                if (coords != null && !coords.isEmpty()) {
                    response.setCoordinates(coords.stream().map(coordinateMapper::toResponse).toList());
                }
            }
        }
        return responses;
    }

    @Override
    public void deleteLand(Long id) {
        landRepository.deleteById(id);
    }

    @Override
    public LandResponse updateLandPicture(Long id, String picture) {
        LandEntity land = landRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Land not found"));
        land.setPicture(picture);
        land = landRepository.save(land);
        LandResponse response = landMapper.toResponse(land);
        
        // Refuerzo manual
        if (response.getCoordinates() == null || response.getCoordinates().isEmpty()) {
            List<com.forestplus.entity.CoordinateEntity> coords = coordinateRepository.findByLandId(response.getId());
            if (coords != null && !coords.isEmpty()) {
                response.setCoordinates(coords.stream().map(coordinateMapper::toResponse).toList());
            }
        }
        return response;
    }
}
