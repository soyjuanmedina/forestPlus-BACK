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
import com.forestplus.security.CurrentUserService;
import com.forestplus.util.SecurityUtils;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
    @Value("${app.frontend.url}")
    String frontendUrl;

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final LoopsService loopsService;
    private final SecurityUtils securityUtils;
    private final CurrentUserService currentUserService;

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
    	
	    // Bloqueamos cualquier intento de un usuario normal de cambiar roles
    	if (request.getRole() != null) {
    	    throw new ForestPlusException(HttpStatus.FORBIDDEN,
    	        "No tienes permisos para asignar roles");
    	}
    	
	    // Bloqueamos cualquier intento de un usuario normal de asignarse a otra compañía
    	if (request.getCompanyId() != null) {
    	    throw new ForestPlusException(HttpStatus.FORBIDDEN,
    	        "No tienes permisos para asignar compañías");
    	}
    	
        return userRepository.findById(id).map(existing -> {
        	
        	// If any change in Loops data communicate to them
        	syncWithLoopsIfNeeded(existing, request.getName(), request.getSurname(), request.getReceiveEmails());
        	
        	if (request.getName() != null) {
                existing.setName(request.getName());
            }
            if (request.getSurname() != null) {
                existing.setSurname(request.getSurname());
            }
            if (request.getSecondSurname() != null) {
                existing.setSecondSurname(request.getSecondSurname());
            }
            if (request.getEmail() != null) {
                existing.setEmail(request.getEmail());
            }
            if (request.getReceiveEmails() != null) {
                existing.setReceiveEmails(request.getReceiveEmails());
            }
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                existing.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            }

            UserEntity updated = userRepository.save(existing);
            return userMapper.toResponse(updated);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    @Override
    public UserResponse updateUserByAdmin(Long id, RegisterUserByAdminRequest request) {
    	
        // Obtener usuario autenticado
        String currentUserRole = currentUserService.getCurrentUserRole();
        Long currentUserCompanyId = currentUserService.getCurrentUserCompanyId();
        
        return userRepository.findById(id).map(existing -> {
        	
        	if (!"ADMIN".equals(currentUserRole)) {

                if (!currentUserCompanyId.equals(existing.getCompany().getId())) {
                    throw new ForestPlusException(HttpStatus.FORBIDDEN,
                        "No tienes permisos para modificar usuarios de otra compañía");
                }

                // COMPANY_ADMIN no puede asignar roles distintos a COMPANY_ADMIN o COMPANY_USER
                if (request.getRole() != null &&
                	    !"COMPANY_ADMIN".equals(request.getRole()) &&
                	    !"COMPANY_USER".equals(request.getRole())) {
                    throw new ForestPlusException(HttpStatus.FORBIDDEN,
                        "No tienes permisos para asignar roles");
                }

                // COMPANY_ADMIN no puede cambiar la compañía
                if (request.getCompanyId() != null &&
                    !request.getCompanyId().equals(existing.getCompany().getId())) {
                    throw new ForestPlusException(HttpStatus.FORBIDDEN,
                        "No tienes permisos para cambiar la compañía del usuario");
                }
            }
        	
        	// If any change in Loops data communicate to them
        	syncWithLoopsIfNeeded(existing, request.getName(), request.getSurname(), request.getReceiveEmails());
        	
        	if (request.getName() != null) {
        	    existing.setName(request.getName());
        	}
        	if (request.getSurname() != null) {
        	    existing.setSurname(request.getSurname());
        	}
        	if (request.getSecondSurname() != null) {
        	    existing.setSecondSurname(request.getSecondSurname());
        	}
        	if (request.getEmail() != null) {
        	    existing.setEmail(request.getEmail());
        	}
        	if (request.getReceiveEmails() != null) {
        	    existing.setReceiveEmails(request.getReceiveEmails());
        	}
        	if (request.getRole() != null) {
        	    existing.setRole(request.getRole());
        	}

            // --- Actualizar compañía ---
            if (request.getCompanyId() != null) {
                CompanyEntity company = companyRepository.findById(request.getCompanyId())
                        .orElseThrow(() -> new RuntimeException("Company not found with id " + request.getCompanyId()));
                existing.setCompany(company);
            }
            
            // Actualizar árboles pendientes
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
            // 1️⃣ Buscar el usuario
            UserEntity user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        	
            // Eliminar en Loops primero
            loopsService.deleteContact(user.getEmail());
            
            userRepository.deleteById(id);

        } catch (DataIntegrityViolationException ex) {
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
        return getUsers(pageable, null, null);
    }
    
    @Override
    public Page<UserResponse> getUsers(Pageable pageable, String role, Long companyId) {
        String currentRole = currentUserService.getCurrentUserRole();
        Long currentCompanyId = currentUserService.getCurrentUserCompanyId();
        
        Page<UserEntity> page;

        if ("ADMIN".equals(currentRole)) {
            if (role != null && companyId != null) {
                page = userRepository.findByRoleAndCompanyId(role, companyId, pageable);
            } else if (role != null) {
                page = userRepository.findByRole(role, pageable);
            } else if (companyId != null) {
                page = userRepository.findByCompanyId(companyId, pageable);
            } else {
                page = userRepository.findAll(pageable);
            }
        } else if ("COMPANY_ADMIN".equals(currentRole)) {
            Long companyFilter = companyId != null ? companyId : currentCompanyId;
            if (role != null) {
                page = userRepository.findByRoleAndCompanyId(role, companyFilter, pageable);
            } else {
                page = userRepository.findByCompanyId(companyFilter, pageable);
            }
        } else {
            page = Page.empty();
        }

        return page.map(userMapper::toResponse);
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
    public UserResponse updateUserPicture(Long id, String picture) {
        Long authUserId = securityUtils.getAuthenticatedUserId();
        boolean isAdmin = securityUtils.isAdmin();
        
        if (!isAdmin && !authUserId.equals(id)) {
            throw new ForestPlusException(HttpStatus.FORBIDDEN, "No puedes modificar la foto de otro usuario");
        }

        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        user.setPicture(picture);
        userRepository.save(user);
        
        return userMapper.toResponse(user);
    }
}
