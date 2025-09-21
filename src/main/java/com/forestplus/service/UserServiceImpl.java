package com.forestplus.service;

import com.forestplus.entity.UserEntity;
import com.forestplus.mapper.UserMapper;
import com.forestplus.repository.UserRepository;
import com.forestplus.request.RegisterUserByAdminRequest;
import com.forestplus.request.RegisterUserRequest;
import com.forestplus.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    // Obtener todos los usuarios
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Obtener usuario por id
    @Override
    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    // Registro estándar (usuario se registra por sí mismo)
    @Override
    public UserResponse registerUser(RegisterUserRequest request) {
        UserEntity user = userMapper.toEntity(request, passwordEncoder);
        UserEntity saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    // Registro realizado por un ADMIN, con contraseña generada
    public UserResponse registerUserByAdmin(RegisterUserByAdminRequest request) {
        String randomPassword = generateRandomPassword(8);
        UserEntity user = userMapper.toEntityWithPassword(request, randomPassword, passwordEncoder);
        UserEntity saved = userRepository.save(user);
        // Aquí podrías enviar el email con la contraseña "randomPassword"
        return userMapper.toResponse(saved);
    }

    // Actualización de usuario (solo campos modificables)
    @Override
    public UserResponse updateUser(Long id, RegisterUserRequest request) {
        return userRepository.findById(id).map(existing -> {
            existing.setName(request.getName());
            existing.setSurname(request.getSurname());
            existing.setSecondSurname(request.getSecondSurname());
            existing.setEmail(request.getEmail());
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                existing.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            }
            existing.setRole(request.getRole());
            UserEntity updated = userRepository.save(existing);
            return userMapper.toResponse(updated);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }
    
    // Actualización de usuario (solo campos modificables)
    @Override
    public UserResponse updateUserByAdmin(Long id, RegisterUserByAdminRequest request) {
        return userRepository.findById(id).map(existing -> {
            existing.setName(request.getName());
            existing.setSurname(request.getSurname());
            existing.setSecondSurname(request.getSecondSurname());
            existing.setEmail(request.getEmail());
//            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
//                existing.setPasswordHash(passwordEncoder.encode(request.getPassword()));
//            }
            existing.setRole(request.getRole());
            UserEntity updated = userRepository.save(existing);
            return userMapper.toResponse(updated);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    // Borrar usuario
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Generar contraseña aleatoria
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int idx = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }
}
