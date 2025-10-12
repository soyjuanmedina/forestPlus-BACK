package com.forestplus.service;

import com.forestplus.dto.request.RegisterUserByAdminRequest;
import com.forestplus.dto.request.RegisterUserRequest;
import com.forestplus.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    // Obtener usuarios paginados sin filtros
    Page<UserResponse> getUsers(Pageable pageable);

    // Obtener usuarios paginados con filtros opcionales de rol y compañía
    Page<UserResponse> getUsers(Pageable pageable, String role, Long companyId);

    // Obtener todos los usuarios (sin paginación)
    List<UserResponse> getAllUsers();

    // Obtener un usuario por ID
    UserResponse getUserById(Long id);

    // Obtener un usuario por email
    UserResponse getUserByEmail(String email);

    // Registrar un usuario por admin
    UserResponse registerUserByAdmin(RegisterUserByAdminRequest request);

    // Actualizar un usuario propio
    UserResponse updateUser(Long id, RegisterUserRequest user);

    // Actualizar un usuario por admin
    UserResponse updateUserByAdmin(Long id, RegisterUserByAdminRequest user);

    // Eliminar un usuario
    void deleteUser(Long id);
}
