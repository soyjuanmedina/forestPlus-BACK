package com.forestplus.request;

import lombok.Data;

@Data
public class RegisterUserRequest {
    private String name;
    private String surname;
    private String secondSurname;
    private String email;
    private String password;
    private String role;
}