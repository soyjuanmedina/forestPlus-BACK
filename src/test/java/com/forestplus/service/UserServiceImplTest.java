package com.forestplus.service;

import com.forestplus.dto.request.RegisterUserByAdminRequest;
import com.forestplus.dto.request.RegisterUserRequest;
import com.forestplus.dto.response.UserResponse;
import com.forestplus.entity.CompanyEntity;
import com.forestplus.entity.UserEntity;
import com.forestplus.exception.EmailAlreadyExistsException;
import com.forestplus.exception.ForestPlusException;
import com.forestplus.integrations.loops.LoopsService;
import com.forestplus.mapper.UserMapper;
import com.forestplus.model.RolesEnum;
import com.forestplus.repository.CompanyRepository;
import com.forestplus.repository.UserRepository;
import com.forestplus.security.CurrentUserService;
import com.forestplus.util.SecurityUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private LoopsService loopsService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ===================== GET USERS =====================
    @Test
    void testGetAllUsers() {
        UserEntity entity = new UserEntity();
        UserResponse response = new UserResponse();

        when(userRepository.findAll()).thenReturn(List.of(entity));
        when(userMapper.toResponse(entity)).thenReturn(response);

        List<UserResponse> result = service.getAllUsers();

        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }

    @Test
    void testGetUserById_found() {
        UserEntity entity = new UserEntity();
        UserResponse response = new UserResponse();

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(userMapper.toResponse(entity)).thenReturn(response);

        UserResponse result = service.getUserById(1L);

        assertSame(response, result);
    }

    @Test
    void testGetUserById_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getUserById(1L));
        assertEquals("User not found with id 1", ex.getMessage());
    }

    // ===================== REGISTER BY ADMIN =====================
    @Test
    void testRegisterUserByAdmin_success() {
        RegisterUserByAdminRequest request = new RegisterUserByAdminRequest();
        request.setEmail("test@test.com");
        request.setName("Test");

        UserEntity savedUser = new UserEntity();
        UserResponse response = new UserResponse();

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(response);

        UserResponse result = service.registerUserByAdmin(request);

        assertSame(response, result);
        verify(loopsService).sendEvent(any());
    }

    @Test
    void testRegisterUserByAdmin_emailExists() {
        RegisterUserByAdminRequest request = new RegisterUserByAdminRequest();
        request.setEmail("exists@test.com");

        when(userRepository.findByEmail("exists@test.com"))
                .thenReturn(Optional.of(new UserEntity()));

        assertThrows(EmailAlreadyExistsException.class, () -> service.registerUserByAdmin(request));
    }

    // ===================== UPDATE USER PICTURE =====================
    @Test
    void testUpdateUserPicture_success() {
        MultipartFile file = mock(MultipartFile.class);
        UserEntity user = new UserEntity();
        UserResponse response = new UserResponse();

        when(securityUtils.getAuthenticatedUserId()).thenReturn(1L);
        when(securityUtils.isAdmin()).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(fileStorageService.storeFile(any(MultipartFile.class), eq("users"), anyString()))
        .thenReturn("url");
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = service.updateUserPicture(1L, file);

        assertSame(response, result);
        assertEquals("url", user.getPicture());
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUserPicture_forbidden() {
        MultipartFile file = mock(MultipartFile.class);
        when(securityUtils.getAuthenticatedUserId()).thenReturn(2L);
        when(securityUtils.isAdmin()).thenReturn(false);

        ForestPlusException ex = assertThrows(ForestPlusException.class,
                () -> service.updateUserPicture(1L, file));

        assertEquals(HttpStatus.FORBIDDEN.value(), ex.getStatus());
    }

    // ===================== DELETE USER =====================
    @Test
    void testDeleteUser_success() {
        doNothing().when(userRepository).deleteById(1L);
        assertDoesNotThrow(() -> service.deleteUser(1L));
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUser_constraintViolation() {
        doThrow(new org.springframework.dao.DataIntegrityViolationException("constraint"))
                .when(userRepository).deleteById(1L);

        ForestPlusException ex = assertThrows(ForestPlusException.class,
                () -> service.deleteUser(1L));

        assertEquals(HttpStatus.CONFLICT.value(), ex.getStatus());
        assertTrue(ex.getMessage().contains("constraint"));
    }

    // ===================== UPDATE USER =====================
    @Test
    void testUpdateUser_roleForbidden() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setRole(RolesEnum.USER);

        ForestPlusException ex = assertThrows(ForestPlusException.class,
                () -> service.updateUser(1L, request));

        assertEquals(HttpStatus.FORBIDDEN.value(), ex.getStatus());
    }

    @Test
    void testUpdateUser_companyForbidden() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setCompanyId(1L);

        ForestPlusException ex = assertThrows(ForestPlusException.class,
                () -> service.updateUser(1L, request));

        assertEquals(HttpStatus.FORBIDDEN.value(), ex.getStatus());
    }

    @Test
    void testUpdateUser_success() {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setName("NewName");

        UserEntity user = new UserEntity();
        UserEntity saved = new UserEntity();
        UserResponse response = new UserResponse();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(response);

        UserResponse result = service.updateUser(1L, request);

        assertSame(response, result);
        assertEquals("NewName", user.getName());
        verify(userRepository).save(user);
    }
}