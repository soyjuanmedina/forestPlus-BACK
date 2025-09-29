package com.forestplus.service;

import com.forestplus.dto.request.LandRequest;
import com.forestplus.dto.response.LandResponse;

import java.util.List;

public interface LandService {
    List<LandResponse> getAllLands();
    LandResponse getLandById(Long id);
    LandResponse createLand(LandRequest request);
    LandResponse updateLand(Long id, LandRequest request);
    void deleteLand(Long id);
}
