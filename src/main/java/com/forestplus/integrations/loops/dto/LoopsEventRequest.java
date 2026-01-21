package com.forestplus.integrations.loops.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoopsEventRequest {

    private String email;
    private String eventName;
    private Map<String, Object> eventProperties;
}