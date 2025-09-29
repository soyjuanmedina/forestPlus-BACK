package com.forestplus.service;

import com.forestplus.dto.request.LandRequest;
import com.forestplus.dto.response.LandResponse;
import com.forestplus.entity.LandEntity;
import com.forestplus.mapper.LandMapper;
import com.forestplus.repository.LandRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LandServiceImpl implements LandService {

    private final LandRepository landRepository;
    private final LandMapper landMapper;

    public LandServiceImpl(LandRepository landRepository, LandMapper landMapper) {
        this.landRepository = landRepository;
        this.landMapper = landMapper;
    }

    @Override
    public List<LandResponse> getAllLands() {
        return landRepository.findAll()
                .stream()
                .map(landMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LandResponse getLandById(Long id) {
        LandEntity land = landRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Land not found with id " + id));
        return landMapper.toResponse(land);
    }

    @Override
    public LandResponse createLand(LandRequest request) {
        LandEntity land = landMapper.toEntity(request);
        LandEntity saved = landRepository.save(land);
        return landMapper.toResponse(saved);
    }

    @Override
    public LandResponse updateLand(Long id, LandRequest request) {
        LandEntity updated = landRepository.findById(id)
                .map(existing -> {
                    existing.setName(request.getName());
                    existing.setLocation(request.getLocation());
                    existing.setArea(request.getArea());

                    // Actualizar usuarios y compañías si vienen en el request
                    if (request.getUserIds() != null) {
                        existing.setUsers(
                                request.getUserIds().stream()
                                        .map(userId -> {
                                            var user = new com.forestplus.entity.UserEntity();
                                            user.setId(userId);
                                            return user;
                                        })
                                        .collect(Collectors.toList())
                        );
                    }

                    if (request.getCompanyIds() != null) {
                        existing.setCompanies(
                                request.getCompanyIds().stream()
                                        .map(companyId -> {
                                            var company = new com.forestplus.entity.CompanyEntity();
                                            company.setId(companyId);
                                            return company;
                                        })
                                        .collect(Collectors.toList())
                        );
                    }

                    return landRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Land not found with id " + id));

        return landMapper.toResponse(updated);
    }

    @Override
    public void deleteLand(Long id) {
        if (!landRepository.existsById(id)) {
            throw new RuntimeException("Land not found with id " + id);
        }
        landRepository.deleteById(id);
    }
}
