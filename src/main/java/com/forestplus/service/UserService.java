package com.forestplus.service;

import com.forestplus.entity.UserEntity;
import com.forestplus.request.RegisterUserByAdminRequest;
import com.forestplus.request.RegisterUserRequest;
import com.forestplus.response.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse registerUser(RegisterUserRequest user);
    UserResponse registerUserByAdmin(RegisterUserByAdminRequest request);
    UserResponse updateUser(Long id, RegisterUserRequest user);
    UserResponse updateUserByAdmin(Long id, RegisterUserByAdminRequest user);
    void deleteUser(Long id);
}
