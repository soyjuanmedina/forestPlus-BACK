package com.forestplus.request;

import lombok.Data;

@Data
public class RegisterUserByAdminRequest {
    private String name;
    private String surname;
    private String secondSurname;
    private String email;
    private String role;
}
