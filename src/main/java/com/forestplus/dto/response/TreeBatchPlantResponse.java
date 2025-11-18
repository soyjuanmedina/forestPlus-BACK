package com.forestplus.dto.response;

import com.forestplus.dto.request.TreeBatchPlantRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
 public class TreeBatchPlantResponse {
    private int planted;
    private int skipped;
    private String reason;
}