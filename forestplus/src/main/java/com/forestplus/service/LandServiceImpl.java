package com.forestplus.service;

import com.forestplus.entity.LandEntity;
import com.forestplus.repository.LandRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LandServiceImpl implements LandService {

    private final LandRepository landRepository;

    public LandServiceImpl(LandRepository landRepository) {
        this.landRepository = landRepository;
    }

    @Override
    public List<LandEntity> getAllLands() {
        return landRepository.findAll();
    }

    @Override
    public Optional<LandEntity> getLandById(Long id) {
        return landRepository.findById(id);
    }

    @Override
    public LandEntity createLand(LandEntity land) {
        return landRepository.save(land);
    }

    @Override
    public LandEntity updateLand(Long id, LandEntity land) {
        return landRepository.findById(id)
                .map(existing -> {
                    existing.setName(land.getName());
                    existing.setLocation(land.getLocation());
                    existing.setArea(land.getArea());
                    existing.setCompany(land.getCompany());
                    return landRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Land not found with id " + id));
    }

    @Override
    public void deleteLand(Long id) {
        landRepository.deleteById(id);
    }
}
