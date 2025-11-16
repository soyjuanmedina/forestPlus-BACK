package com.forestplus.dto.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LandUpdateRequest {
    private String name;
    private String location;
    private BigDecimal area;
    private String picture;
    private Integer maxTrees;
}
