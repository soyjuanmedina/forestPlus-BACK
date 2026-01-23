package com.forestplus.integrations.loops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactIdentity {
    private String id;
    private String email;
    private String userId;
}
