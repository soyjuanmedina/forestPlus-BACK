package com.forestplus.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyEmissionResponse {
    private Long id;
    private Long companyId;
    private String companyName;
    private Integer year;
    private Double totalEmissions;
    private String createdAt;
}
