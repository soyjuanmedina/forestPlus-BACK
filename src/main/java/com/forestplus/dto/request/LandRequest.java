package com.forestplus.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LandRequest {
    private String name;
    private String location;
    private BigDecimal  area; // se convertirá a BigDecimal en el service
    private List<Long> userIds;
    private List<Long> companyIds;
    private String picture;
}
