package com.forestplus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String surname;
    private String secondSurname;
    private String email;
    private String password;
    private String role;
}
