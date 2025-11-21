package com.forestplus.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoordinateRequest {

    private Long landId;
    private Double latitude;
    private Double longitude;
}
