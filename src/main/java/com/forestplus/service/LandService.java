package com.forestplus.service;

import com.forestplus.dto.request.LandRequest;
import com.forestplus.dto.request.LandUpdateRequest;
import com.forestplus.dto.response.LandResponse;

import java.util.List;

public interface LandService {

    LandResponse createLand(LandRequest request);

    LandResponse updateLand(Long id, LandUpdateRequest request);

    LandResponse getLandById(Long id);

    List<LandResponse> getAllLands();

    void deleteLand(Long id);
}
