package com.forestplus.dto.request;

import com.forestplus.dto.response.CompanyResponse;
import com.forestplus.dto.response.UserResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyUpdateRequest  {
    private String name;
    private String address;
}
