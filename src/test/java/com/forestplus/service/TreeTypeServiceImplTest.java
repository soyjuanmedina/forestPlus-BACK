package com.forestplus.service;

import com.forestplus.dto.request.TreeTypeRequest;
import com.forestplus.dto.request.TreeTypeUpdateRequest;
import com.forestplus.dto.response.TreeTypeResponse;
import com.forestplus.entity.TreeTypeEntity;
import com.forestplus.exception.ForestPlusException;
import com.forestplus.mapper.TreeTypeMapper;
import com.forestplus.repository.TreeTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TreeTypeServiceImplTest {

    @InjectMocks
    private TreeTypeServiceImpl service;

    @Mock
    private TreeTypeRepository repository;

    @Mock
    private TreeTypeMapper mapper;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(service, "entityManager", entityManager);
    }

    @Test
    void testGetAllTreeTypes() {
        TreeTypeEntity entity = new TreeTypeEntity();
        TreeTypeResponse response = new TreeTypeResponse();

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        List<TreeTypeResponse> result = service.getAllTreeTypes();

        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }

    @Test
    void testGetTreeTypeById_found() {
        TreeTypeEntity entity = new TreeTypeEntity();
        TreeTypeResponse response = new TreeTypeResponse();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        TreeTypeResponse result = service.getTreeTypeById(1L);

        assertSame(response, result);
    }

    @Test
    void testGetTreeTypeById_notFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        ForestPlusException ex = assertThrows(ForestPlusException.class, () -> service.getTreeTypeById(1L));
        assertEquals(HttpStatus.NOT_FOUND.value(), ex.getStatus());
    }

    @Test
    void testCreateTreeType() {
        TreeTypeRequest request = new TreeTypeRequest();
        TreeTypeEntity entity = new TreeTypeEntity();
        TreeTypeEntity saved = new TreeTypeEntity();
        TreeTypeResponse response = new TreeTypeResponse();

        when(mapper.toEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        TreeTypeResponse result = service.createTreeType(request);

        assertSame(response, result);
        verify(repository).save(entity);
    }

    @Test
    void testUpdateTreeType_found() {
        TreeTypeUpdateRequest request = new TreeTypeUpdateRequest();
        TreeTypeEntity entity = new TreeTypeEntity();
        TreeTypeEntity saved = new TreeTypeEntity();
        TreeTypeResponse response = new TreeTypeResponse();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        doNothing().when(mapper).updateEntityFromDto(request, entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        TreeTypeResponse result = service.updateTreeType(1L, request);

        assertSame(response, result);
        verify(mapper).updateEntityFromDto(request, entity);
        verify(repository).save(entity);
    }

    @Test
    void testUpdateTreeType_notFound() {
        TreeTypeUpdateRequest request = new TreeTypeUpdateRequest();
        when(repository.findById(1L)).thenReturn(Optional.empty());

        ForestPlusException ex = assertThrows(ForestPlusException.class,
                () -> service.updateTreeType(1L, request));
        assertEquals(HttpStatus.NOT_FOUND.value(), ex.getStatus());
    }

    @Test
    void testDeleteTreeType_found() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteTreeType(1L);

        verify(repository).deleteById(1L);
        verify(entityManager).flush();
    }

    @Test
    void testDeleteTreeType_notFound() {
        when(repository.existsById(1L)).thenReturn(false);

        ForestPlusException ex = assertThrows(ForestPlusException.class,
                () -> service.deleteTreeType(1L));
        assertEquals(HttpStatus.NOT_FOUND.value(), ex.getStatus());
    }

    @Test
    void testUpdateTreeTypePicture() {
        TreeTypeEntity entity = new TreeTypeEntity();
        TreeTypeResponse response = new TreeTypeResponse();
        MultipartFile file = mock(MultipartFile.class);

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(fileStorageService.storeFile(eq(file), eq("tree-types"), anyString()))
        .thenReturn("url");
        when(mapper.toResponse(entity)).thenReturn(response);

        TreeTypeResponse result = service.updateTreeTypePicture(1L, file);

        assertSame(response, result);
        assertEquals("url", entity.getPicture());
        verify(repository).save(entity);
    }
}