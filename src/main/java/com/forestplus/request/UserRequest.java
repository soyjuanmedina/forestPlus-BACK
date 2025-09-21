package com.forestplus.request;

import lombok.Data;

@Data
public class UserRequest {
    private String name;
    private String surname;
    private String secondSurname;
    private String email;
    private String password;
    private String role;
}