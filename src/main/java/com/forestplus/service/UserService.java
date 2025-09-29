package com.forestplus.service;

import com.forestplus.dto.request.RegisterUserByAdminRequest;
import com.forestplus.dto.request.RegisterUserRequest;
import com.forestplus.dto.response.UserResponse;
import com.forestplus.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse getUserByEmail(String email);
    UserResponse registerUserByAdmin(RegisterUserByAdminRequest request);
    UserResponse updateUser(Long id, RegisterUserRequest user);
    UserResponse updateUserByAdmin(Long id, RegisterUserByAdminRequest user);
    void deleteUser(Long id);
}
