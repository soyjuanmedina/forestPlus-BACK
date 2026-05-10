package com.forestplus.service;

import com.forestplus.dto.request.LandRequest;
import com.forestplus.dto.request.LandUpdateRequest;
import com.forestplus.dto.response.LandResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.LandEntity;
import com.forestplus.entity.UserEntity;
import com.forestplus.exception.ResourceNotFoundException;
import com.forestplus.exception.ForestPlusException;
import org.springframework.http.HttpStatus;
import com.forestplus.mapper.LandMapper;
import com.forestplus.repository.CompanyRepository;
import com.forestplus.repository.LandRepository;
import com.forestplus.repository.UserRepository;
import com.forestplus.repository.TreeRepository;
import com.forestplus.repository.PlannedPlantationRepository;
import com.forestplus.security.CurrentUserService;

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
    private final TreeRepository treeRepository;
    private final PlannedPlantationRepository plannedPlantationRepository;
    private final CurrentUserService currentUserService;


    @Override
    @org.springframework.transaction.annotation.Transactional
    public LandResponse createLand(LandRequest request) {
        // 1. Convertir el DTO de entrada a la Entidad base
        LandEntity land = landMapper.toEntity(request);
        if (land == null) {
            throw new RuntimeException("Error mapping land request");
        }

        // 2. Obtener información del contexto de seguridad
        Long currentUserId = currentUserService.getCurrentUserId();
        String currentRole = currentUserService.getCurrentUserRole();

        // 3. Lógica de vinculación con la Compañía (Solo para COMPANY_ADMIN)
        if ("COMPANY_ADMIN".equals(currentRole)) {
            UserEntity creator = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("ERRORS.USER.NOT_FOUND"));

            if (creator.getCompany() != null) {
                CompanyEntity company = creator.getCompany();
                
                // Inicializar las listas si vienen nulas para evitar NullPointerException
                if (land.getCompanies() == null) land.setCompanies(new java.util.ArrayList<>());
                if (company.getLands() == null) company.setLands(new java.util.ArrayList<>());

                // Establecer vínculo bidireccional
                // Importante: Al añadir el terreno a la lista de la compañía (lado dueño),
                // JPA se encarga de insertar en la tabla 'company_lands'.
                land.getCompanies().add(company);
                company.getLands().add(land);
            }
        }

        // 4. Vincular las coordenadas con el terreno (Relación OneToMany)
        if (land.getCoordinates() != null) {
            for (com.forestplus.entity.CoordinateEntity coord : land.getCoordinates()) {
                coord.setLand(land);
            }
        }

        // 5. Guardar la entidad (Esto persiste Land, Coordinates y la relación con Company)
        land = landRepository.save(land);

        // 6. Mapear a respuesta y refuerzo de datos para el Front-end
        LandResponse response = landMapper.toResponse(land);
        
        // Si el mapper no cargó las coordenadas automáticamente, las forzamos
        if (response.getCoordinates() == null || response.getCoordinates().isEmpty()) {
            List<com.forestplus.entity.CoordinateEntity> coords = coordinateRepository.findByLandId(land.getId());
            if (coords != null && !coords.isEmpty()) {
                response.setCoordinates(coords.stream()
                    .map(coordinateMapper::toResponse)
                    .toList());
            }
        }

        enrichWithTreeCount(response);
        return response;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public LandResponse updateLand(Long id, LandUpdateRequest request) {
        LandEntity land = landRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERRORS.LAND.NOT_FOUND"));

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
        enrichWithTreeCount(response);
        return response;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public LandResponse getLandById(Long id) {
        LandEntity land = landRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERRORS.LAND.NOT_FOUND"));
        LandResponse response = landMapper.toResponse(land);
        
        // Refuerzo manual por si el mapper tiene problemas con la carga diferida
        if (response.getCoordinates() == null || response.getCoordinates().isEmpty()) {
            List<com.forestplus.entity.CoordinateEntity> coords = coordinateRepository.findByLandId(id);
            if (coords != null && !coords.isEmpty()) {
                response.setCoordinates(coords.stream().map(coordinateMapper::toResponse).toList());
            }
        }
        enrichWithTreeCount(response);
        return response;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<LandResponse> getAllLands(Long companyId) {
        List<LandEntity> lands;
    	if (companyId != null) {
            // Buscamos solo las de la compañía
            lands = landRepository.findByCompanies_Id(companyId);
        } else {
            // Buscamos todas (para el ADMIN)
            lands = landRepository.findAll();
        }
    	

        List<LandResponse> responses = landMapper.toResponseList(lands);
        
        // Refuerzo para todos los terrenos
        for (LandResponse response : responses) {
            if (response.getCoordinates() == null || response.getCoordinates().isEmpty()) {
                List<com.forestplus.entity.CoordinateEntity> coords = coordinateRepository.findByLandId(response.getId());
                if (coords != null && !coords.isEmpty()) {
                    response.setCoordinates(coords.stream().map(coordinateMapper::toResponse).toList());
                }
            }
            enrichWithTreeCount(response);
        }
        return responses;
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void deleteLand(Long id) {
        LandEntity land = landRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERRORS.LAND.NOT_FOUND"));

        if (treeRepository.countByLandId(id) > 0) {
            throw new ForestPlusException(HttpStatus.BAD_REQUEST, "ERRORS.LAND.HAS_TREES");
        }

        if (plannedPlantationRepository.countByLandId(id) > 0) {
            throw new ForestPlusException(HttpStatus.BAD_REQUEST, "ERRORS.LAND.HAS_PLANTATIONS");
        }

        // Desvincular de compañías (lado dueño de la relación ManyToMany)
        if (land.getCompanies() != null) {
            for (CompanyEntity company : new java.util.ArrayList<>(land.getCompanies())) {
                company.getLands().remove(land);
            }
            land.getCompanies().clear();
        }
        
        // Desvincular de usuarios
        if (land.getUsers() != null) {
            land.getUsers().clear();
        }

        landRepository.delete(land);
    }

    @Override
    public LandResponse updateLandPicture(Long id, String picture) {
        LandEntity land = landRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ERRORS.LAND.NOT_FOUND"));
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
        enrichWithTreeCount(response);
        return response;
    }

    private void enrichWithTreeCount(LandResponse response) {
        if (response != null && response.getId() != null) {
            response.setPlantedTreesCount(treeRepository.countByLandId(response.getId()));
        }
    }
}
