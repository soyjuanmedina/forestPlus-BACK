package com.forestplus.dto.response;

import com.forestplus.dto.request.CoordinateUpdateRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoordinateResponse {
    private Long id;
    private Double latitude;
    private Double longitude;
    private Long landId;
    private String landName;
}