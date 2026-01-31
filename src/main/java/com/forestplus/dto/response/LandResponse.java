package com.forestplus.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LandResponse {
    private Long id;
    private String name;
    private String description;
    private String location;
    private BigDecimal area;
    private String picture;
    private Integer maxTrees;
    private LocalDateTime createdAt;

    private List<Long> userIds;
    private List<Long> companyIds;
}
