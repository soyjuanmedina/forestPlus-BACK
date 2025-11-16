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

    @Override
    public LandResponse createLand(LandRequest request) {

        LandEntity land = LandEntity.builder()
                .name(request.getName())
                .location(request.getLocation())
                .area(request.getArea())
                .picture(request.getPicture())
                .maxTrees(null) // se añade luego si quieres
                .build();

        // SIN asignación de users/companies aquí (se hará con endpoints específicos)
        land = landRepository.save(land);

        return landMapper.toResponse(land);
    }

    @Override
    public LandResponse updateLand(Long id, LandUpdateRequest request) {
        LandEntity land = landRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Land not found"));

        land.setName(request.getName());
        land.setLocation(request.getLocation());
        land.setArea(request.getArea());
        land.setPicture(request.getPicture());
        land.setMaxTrees(request.getMaxTrees());

        land = landRepository.save(land);
        return landMapper.toResponse(land);
    }

    @Override
    public LandResponse getLandById(Long id) {
        LandEntity land = landRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Land not found"));
        return landMapper.toResponse(land);
    }

    @Override
    public List<LandResponse> getAllLands() {
        List<LandEntity> lands = landRepository.findAll();
        return landMapper.toResponseList(lands);
    }

    @Override
    public void deleteLand(Long id) {
        landRepository.deleteById(id);
    }

}
