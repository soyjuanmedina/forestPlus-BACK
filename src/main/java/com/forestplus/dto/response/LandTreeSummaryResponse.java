package com.forestplus.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LandTreeSummaryResponse {
    Long treeTypeId;
    String treeTypeName;
    String picture;
    Long quantity;
}
