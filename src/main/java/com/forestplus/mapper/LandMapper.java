package com.forestplus.mapper;



import java.util.List;

import org.mapstruct.Mapper;

import com.forestplus.dto.request.LandRequest;
import com.forestplus.dto.response.LandResponse;
import com.forestplus.entity.LandEntity;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface LandMapper {

    // --- ENTITY -> RESPONSE ---
    LandResponse toResponse(LandEntity land);

    List<LandResponse> toResponseList(List<LandEntity> lands);

    // --- REQUEST -> ENTITY ---
    LandEntity toEntity(LandRequest request);
}
