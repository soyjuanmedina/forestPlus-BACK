package com.forestplus.service;

import com.forestplus.dto.request.RegisterUserByAdminRequest;
import com.forestplus.dto.request.RegisterUserRequest;
import com.forestplus.dto.response.UserResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.UserEntity;
import com.forestplus.exception.EmailAlreadyExistsException;
import com.forestplus.exception.ForestPlusException;
import com.forestplus.integrations.loops.LoopsService;
import com.forestplus.integrations.loops.dto.LoopsEventRequest;
import com.forestplus.mapper.UserMapper;
import com.forestplus.model.RolesEnum;
import com.forestplus.repository.CompanyRepository;
import com.forestplus.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository; // <--- nuevo
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final FileStorageService fileStorageService;
    private final LoopsService loopsService;

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

        UserEntity user = UserEntity.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .secondSurname(request.getSecondSurname())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(randomPassword))
                .role(request.getRole() != null ? request.getRole() : RolesEnum.USER)
                .forcePasswordChange(true)
                .emailVerified(true)
                .uuid(null)
                .company(request.getCompanyId() != null
                         ? companyRepository.findById(request.getCompanyId())
                               .orElseThrow(() -> new RuntimeException("Company not found with id " + request.getCompanyId()))
                         : null)
                .build();

        UserEntity saved = userRepository.save(user);

        // Enviar email con la password desde Loops      
        String link = frontendUrl;
        
        Map<String, Object> eventProperties = new HashMap<>();
        eventProperties.put("link", link);
        eventProperties.put("password", randomPassword);

        LoopsEventRequest loopsEvent = new LoopsEventRequest(
        	saved.getEmail(),
            "register_user_by_admin",
            eventProperties
        );
        
        loopsService.sendEvent(loopsEvent);

        return userMapper.toResponse(saved);
    }

    @Override
    public UserResponse updateUser(Long id, RegisterUserRequest request) {
        return userRepository.findById(id).map(existing -> {
        	
        	// If any change in Loops data communicate to them
        	syncWithLoopsIfNeeded(existing, request.getName(), request.getSurname(), request.getReceiveEmails());
        	
            existing.setName(request.getName());
            existing.setSurname(request.getSurname());
            existing.setSecondSurname(request.getSecondSurname());
            existing.setEmail(request.getEmail());
            existing.setReceiveEmails(request.getReceiveEmails());

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
        	
        	// If any change in Loops data communicate to them
        	syncWithLoopsIfNeeded(existing, request.getName(), request.getSurname(), request.getReceiveEmails());
        	
            existing.setName(request.getName());
            existing.setSurname(request.getSurname());
            existing.setSecondSurname(request.getSecondSurname());
            existing.setEmail(request.getEmail());
            existing.setRole(request.getRole());
            existing.setReceiveEmails(request.getReceiveEmails());

            // --- Actualizar compañía ---
            if (request.getCompanyId() != null) {
                CompanyEntity company = companyRepository.findById(request.getCompanyId())
                        .orElseThrow(() -> new RuntimeException("Company not found with id " + request.getCompanyId()));
                existing.setCompany(company);
            }
            
            // ✅ NUEVO: actualizar árboles pendientes
            if (request.getPendingTreesCount() != null) {
                if (request.getPendingTreesCount() < 0) {
                    throw new ForestPlusException(
                        HttpStatus.BAD_REQUEST,
                        "pendingTreesCount no puede ser negativo"
                    ) {};
                }
                existing.setPendingTreesCount(request.getPendingTreesCount());
            }

            UserEntity updated = userRepository.save(existing);
            return userMapper.toResponse(updated);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }
    
    private void syncWithLoopsIfNeeded(
    	    UserEntity existing,
    	    String name,
    	    String surname,
    	    Boolean receiveEmails
    	) {
    	    if (!Objects.equals(existing.getName(), name) ||
    	        !Objects.equals(existing.getSurname(), surname) ||
    	        !Objects.equals(existing.getReceiveEmails(), receiveEmails)) {

    	        log.info("Updating user in Loops");
    	        Map<String, Object> contactProperties = new HashMap<>();
    	        contactProperties.put("firstName", name);
    	        contactProperties.put("lastName", surname);
    	        contactProperties.put("subscribed", receiveEmails);

    	        loopsService.upsertContact(existing.getEmail(), contactProperties);
    	    }
    	}

    @Override
    public void deleteUser(Long id) {
        try {
            userRepository.deleteById(id);

        } catch (DataIntegrityViolationException ex) {
            // Obtienes la causa real de MariaDB
            Throwable root = ex.getMostSpecificCause();
            String originalMessage = root != null ? root.getMessage() : ex.getMessage();

            throw new ForestPlusException(
                    HttpStatus.CONFLICT,
                    originalMessage
            ) {};
        }
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
    
    @Override
    @Transactional
    public UserResponse updateUserPicture(Long id, MultipartFile file) {
        // 1️⃣ Buscar el usuario
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Imprimir info de debug
        System.out.println("📁 Guardando imagen para usuario " + id + ", original filename: " 
                            + file.getOriginalFilename());

        // 2️⃣ Guardar la imagen con id para evitar colisiones
        String imageUrl = fileStorageService.storeFile(file, "users", user.getUuid());
        
        System.out.println("Imagen guardada en: " + imageUrl);

        // 3️⃣ Actualizar entidad con la nueva ruta
        user.setPicture(imageUrl);

        // 4️⃣ Guardar cambios en DB
        userRepository.save(user);

        // 5️⃣ Devolver DTO
        return userMapper.toResponse(user);
    }
}

