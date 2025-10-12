package com.forestplus.service;

import com.forestplus.dto.request.RegisterUserByAdminRequest;
import com.forestplus.dto.request.RegisterUserRequest;
import com.forestplus.dto.response.UserResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.UserEntity;
import com.forestplus.exception.EmailAlreadyExistsException;
import com.forestplus.mapper.UserMapper;
import com.forestplus.repository.CompanyRepository;
import com.forestplus.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository; // <--- nuevo
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EmailService emailService;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("User not found with email " + email));
    }

    @Override
    public UserResponse registerUserByAdmin(RegisterUserByAdminRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        String randomPassword = generateRandomPassword(8);
        UserEntity user = userMapper.toEntity(request);
        user.setPasswordHash(passwordEncoder.encode(randomPassword));
        user.setForcePasswordChange(true);
        user.setEmailVerified(true);
        user.setUuid(null);

        // --- Asignar compañía ---
        if (request.getCompanyId() != null) {
            CompanyEntity company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found with id " + request.getCompanyId()));
            user.setCompany(company);
        }

        UserEntity saved = userRepository.save(user);

        // Enviar email con contraseña generada
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", saved.getName());
        vars.put("email", saved.getEmail());
        vars.put("password", randomPassword);

        emailService.sendEmail(
                saved.getEmail(),
                "Tu nueva cuenta en ForestPlus",
                "contents/new-account-content",
                vars
        );

        return userMapper.toResponse(saved);
    }

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

            // --- Actualizar compañía ---
            if (request.getCompanyId() != null) {
                CompanyEntity company = companyRepository.findById(request.getCompanyId())
                        .orElseThrow(() -> new RuntimeException("Company not found with id " + request.getCompanyId()));
                existing.setCompany(company);
            }

            UserEntity updated = userRepository.save(existing);
            return userMapper.toResponse(updated);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    @Override
    public UserResponse updateUserByAdmin(Long id, RegisterUserByAdminRequest request) {
        return userRepository.findById(id).map(existing -> {
            existing.setName(request.getName());
            existing.setSurname(request.getSurname());
            existing.setSecondSurname(request.getSecondSurname());
            existing.setEmail(request.getEmail());
            existing.setRole(request.getRole());

            // --- Actualizar compañía ---
            if (request.getCompanyId() != null) {
                CompanyEntity company = companyRepository.findById(request.getCompanyId())
                        .orElseThrow(() -> new RuntimeException("Company not found with id " + request.getCompanyId()));
                existing.setCompany(company);
            }

            UserEntity updated = userRepository.save(existing);
            return userMapper.toResponse(updated);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @Override
    public Page<UserResponse> getUsers(Pageable pageable) {
        // simplemente delegamos al método más completo sin filtros
        return getUsers(pageable, null, null);
    }
    
    @Override
    public Page<UserResponse> getUsers(Pageable pageable, String role, Long companyId) {
        Page<UserEntity> page;
        if (role != null && companyId != null) {
            page = userRepository.findByRoleAndCompanyId(role, companyId, pageable);
        } else if (role != null) {
            page = userRepository.findByRole(role, pageable);
        } else if (companyId != null) {
            page = userRepository.findByCompanyId(companyId, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }

        return page.map(user -> userMapper.toResponse(user));
    }

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

