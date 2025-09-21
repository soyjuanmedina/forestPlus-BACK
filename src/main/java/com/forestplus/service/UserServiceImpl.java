package com.forestplus.service;

import com.forestplus.entity.UserEntity;
import com.forestplus.exception.EmailAlreadyExistsException;
import com.forestplus.exception.EmailSendException;
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
    private final EmailService emailService;

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

    // Registro realizado por un ADMIN, con contraseña generada
    public UserResponse registerUserByAdmin(RegisterUserByAdminRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }
        String randomPassword = generateRandomPassword(8);
        UserEntity user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(randomPassword)); // aquí seteas
        UserEntity saved = userRepository.save(user);

        // Enviar email con la contraseña generada
        String subject = "Tu nueva cuenta en ForestPlus";
        String text = "Hola " + saved.getName() + ",\n\n"
                    + "Se ha creado tu cuenta con email: " + saved.getEmail() + "\n"
                    + "Tu contraseña temporal es: " + randomPassword + "\n\n"
                    + "Por favor, cámbiala después de iniciar sesión.";
        emailService.sendEmail(saved.getEmail(), subject, text);

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
