package com.forestplus.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String newPassword;
}