package com.forestplus.service;

import com.forestplus.dto.request.PurchaseRequest;
import com.forestplus.dto.response.PurchaseResponse;
import com.forestplus.entity.LandEntity;
import com.forestplus.entity.TreeTypeEntity;
import com.forestplus.entity.UserEntity;
import com.forestplus.integrations.loops.LoopsService;
import com.forestplus.integrations.loops.dto.LoopsEventRequest;
import com.forestplus.repository.LandRepository;
import com.forestplus.repository.TreeTypeRepository;
import com.forestplus.security.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PurchaseServiceImplTest {

    @InjectMocks
    private PurchaseServiceImpl service;

    @Mock
    private LandRepository landRepository;

    @Mock
    private TreeTypeRepository treeTypeRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private LoopsService loopsService;

    @Mock
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service.frontendUrl = "https://forestplus.com";
    }

    @Test
    void shouldProcessPurchaseSuccessfully() {
        // Datos de entrada
        PurchaseRequest request = new PurchaseRequest();
        request.setLandId(1L);
        request.setTreeTypeId(2L);
        request.setQuantity(5);

        LandEntity land = new LandEntity();
        land.setName("Test Land");

        TreeTypeEntity treeType = new TreeTypeEntity();
        treeType.setName("Oak");

        UserEntity user = new UserEntity();
        user.setName("Test User");
        user.setEmail("test@forestplus.com");
        user.setPendingTreesCount(0);

        // Mocks
        when(landRepository.findById(1L)).thenReturn(Optional.of(land));
        when(treeTypeRepository.findById(2L)).thenReturn(Optional.of(treeType));
        when(currentUserService.getCurrentUser()).thenReturn(user);

        // Ejecutar
        PurchaseResponse response = service.processPurchase(request);

        // Validaciones
        assertNotNull(response);
        assertEquals("Test Land", response.getLandName());
        assertEquals(5, response.getQuantity());
        assertEquals(new BigDecimal("50"), response.getTotalPrice());
        assertEquals(5, user.getPendingTreesCount());

        // Verificar que se enviaron los eventos a Loops
        verify(loopsService, times(2)).sendEvent(any(LoopsEventRequest.class));
    }

    @Test
    void shouldThrowWhenLandNotFound() {
        PurchaseRequest request = new PurchaseRequest();
        request.setLandId(1L);
        request.setTreeTypeId(2L);
        request.setQuantity(5);

        when(landRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.processPurchase(request));
        assertEquals("Terreno no encontrado", ex.getMessage());
    }

    @Test
    void shouldThrowWhenTreeTypeNotFound() {
        PurchaseRequest request = new PurchaseRequest();
        request.setLandId(1L);
        request.setTreeTypeId(2L);
        request.setQuantity(5);

        LandEntity land = new LandEntity();
        land.setName("Test Land");

        when(landRepository.findById(1L)).thenReturn(Optional.of(land));
        when(treeTypeRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.processPurchase(request));
        assertEquals("Tipo de árbol no encontrado", ex.getMessage());
    }

    @Test
    void shouldThrowWhenUserNotAuthenticated() {
        PurchaseRequest request = new PurchaseRequest();
        request.setLandId(1L);
        request.setTreeTypeId(2L);
        request.setQuantity(5);

        LandEntity land = new LandEntity();
        land.setName("Test Land");

        TreeTypeEntity treeType = new TreeTypeEntity();
        treeType.setName("Oak");

        when(landRepository.findById(1L)).thenReturn(Optional.of(land));
        when(treeTypeRepository.findById(2L)).thenReturn(Optional.of(treeType));
        when(currentUserService.getCurrentUser()).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.processPurchase(request));
        assertEquals("Usuario no autenticado", ex.getMessage());
    }
}