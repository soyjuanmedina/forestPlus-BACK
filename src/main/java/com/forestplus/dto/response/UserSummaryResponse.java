package com.forestplus.dto.response;

import lombok.Data;

@Data
public class UserSummaryResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
}