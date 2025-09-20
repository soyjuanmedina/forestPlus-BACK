package com.forestplus.service;

import com.forestplus.entity.LandEntity;

import java.util.List;
import java.util.Optional;

public interface LandService {
    List<LandEntity> getAllLands();
    Optional<LandEntity> getLandById(Long id);
    LandEntity createLand(LandEntity land);
    LandEntity updateLand(Long id, LandEntity land);
    void deleteLand(Long id);
}
