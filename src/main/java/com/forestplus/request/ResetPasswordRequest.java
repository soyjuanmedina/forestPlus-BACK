package com.forestplus.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String newPassword;
}