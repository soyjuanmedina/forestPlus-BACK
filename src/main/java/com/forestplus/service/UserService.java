package com.forestplus.service;

import com.forestplus.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserEntity> getAllUsers();
    Optional<UserEntity> getUserById(Long id);
    UserEntity createUser(UserEntity user);
    UserEntity updateUser(Long id, UserEntity user);
    void deleteUser(Long id);
}
