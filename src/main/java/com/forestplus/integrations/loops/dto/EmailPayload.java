package com.forestplus.integrations.loops.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailPayload {
    private String id;
    private String emailMessageId;
    private String subject;
}
