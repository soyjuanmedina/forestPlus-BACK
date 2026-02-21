package com.forestplus.service;

import com.forestplus.dto.request.TreeBatchPlantRequest;
import com.forestplus.dto.request.TreeRequest;
import com.forestplus.dto.request.TreeUpdateRequest;
import com.forestplus.dto.response.TreeResponse;
import com.forestplus.dto.response.TreeBatchPlantResponse;
import com.forestplus.entity.*;
import com.forestplus.exception.ResourceNotFoundException;
import com.forestplus.mapper.TreeMapper;
import com.forestplus.repository.*;
import com.forestplus.security.CurrentUserService;
import com.forestplus.util.SecurityUtils;
import com.forestplus.integrations.loops.LoopsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TreeServiceImplTest {

    @InjectMocks
    private TreeServiceImpl service;

    @Mock private TreeRepository treeRepository;
    @Mock private TreeTypeRepository treeTypeRepository;
    @Mock private LandRepository landRepository;
    @Mock private UserRepository userRepository;
    @Mock private CompanyRepository companyRepository;
    @Mock private PlannedPlantationRepository plannedPlantationRepository;
    @Mock private TreeMapper treeMapper;
    @Mock private LoopsService loopsService;
    @Mock private CurrentUserService currentUserService;
    @Mock private SecurityUtils securityUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service.frontendUrl = "https://frontend.test";
    }

    @Test
    void testCreateTree_Success() {
        TreeRequest request = new TreeRequest();
        request.setLandId(1L);
        request.setTreeTypeId(2L);

        TreeEntity treeEntity = new TreeEntity();
        TreeResponse treeResponse = new TreeResponse();

        LandEntity land = new LandEntity();
        TreeTypeEntity type = new TreeTypeEntity();

        when(treeMapper.toEntity(request)).thenReturn(treeEntity);
        when(landRepository.findById(1L)).thenReturn(Optional.of(land));
        when(treeTypeRepository.findById(2L)).thenReturn(Optional.of(type));
        when(treeRepository.save(treeEntity)).thenReturn(treeEntity);
        when(treeMapper.toResponse(treeEntity)).thenReturn(treeResponse);

        TreeResponse result = service.createTree(request);

        assertNotNull(result);
        verify(treeRepository).save(treeEntity);
        verify(treeMapper).toResponse(treeEntity);
        assertEquals(treeResponse, result);
        assertEquals(land, treeEntity.getLand());
        assertEquals(type, treeEntity.getTreeType());
    }

    @Test
    void testUpdateTree_AsUser() {
        TreeUpdateRequest request = new TreeUpdateRequest();
        request.setCustomName("MyTree");

        TreeEntity tree = new TreeEntity();
        TreeResponse response = new TreeResponse();

        when(treeRepository.findById(1L)).thenReturn(Optional.of(tree));
        when(securityUtils.isAdmin()).thenReturn(false);
        when(treeRepository.save(tree)).thenReturn(tree);
        when(treeMapper.toResponse(tree)).thenReturn(response);

        TreeResponse result = service.updateTree(1L, request);

        assertEquals(response, result);
        assertEquals("MyTree", tree.getCustomName());
    }

    @Test
    void testUpdateTree_AsAdmin() {
        TreeUpdateRequest request = new TreeUpdateRequest();
        request.setLandId(1L);
        TreeEntity tree = new TreeEntity();
        LandEntity land = new LandEntity();
        TreeResponse response = new TreeResponse();

        when(treeRepository.findById(1L)).thenReturn(Optional.of(tree));
        when(securityUtils.isAdmin()).thenReturn(true);
        when(landRepository.findById(1L)).thenReturn(Optional.of(land));
        doNothing().when(treeMapper).updateEntityFromDto(request, tree);
        when(treeRepository.save(tree)).thenReturn(tree);
        when(treeMapper.toResponse(tree)).thenReturn(response);

        TreeResponse result = service.updateTree(1L, request);

        assertEquals(response, result);
        assertEquals(land, tree.getLand());
        verify(treeMapper).updateEntityFromDto(request, tree);
    }

    @Test
    void testPlantTreeBatch() {
        TreeBatchPlantRequest request = new TreeBatchPlantRequest();
        request.setLandId(1L);
        request.setTreeTypeId(2L);
        request.setQuantity(5);
        request.setOwnerUserId(3L);

        LandEntity land = new LandEntity();
        land.setId(1L);
        land.setName("MyLand");
        land.setMaxTrees(10);

        TreeTypeEntity type = new TreeTypeEntity();
        type.setCo2AbsorptionAt20(new BigDecimal("1.0"));
        type.setCo2AbsorptionAt25(new BigDecimal("2.0"));
        type.setCo2AbsorptionAt30(new BigDecimal("3.0"));
        type.setCo2AbsorptionAt35(new BigDecimal("4.0"));
        type.setCo2AbsorptionAt40(new BigDecimal("5.0"));

        UserEntity owner = new UserEntity();
        owner.setId(3L);
        owner.setPendingTreesCount(5);
        owner.setEmail("user@test.com");

        when(landRepository.findById(1L)).thenReturn(Optional.of(land));
        when(treeTypeRepository.findById(2L)).thenReturn(Optional.of(type));
        when(userRepository.findById(3L)).thenReturn(Optional.of(owner));
        when(treeRepository.countByLandId(1L)).thenReturn(2L);

        TreeEntity treeEntity = new TreeEntity();
        when(treeMapper.toEntity(any(TreeBatchPlantRequest.class))).thenReturn(treeEntity);

        TreeBatchPlantResponse response = service.plantTreeBatch(request);

        assertEquals(5, response.getPlanted());
        assertEquals(0, response.getSkipped());
        assertEquals("OK", response.getReason());
        verify(treeRepository).saveAll(anyList());
        verify(userRepository).save(owner);
        verify(loopsService).sendEvent(any());
    }

    @Test
    void testAssignTreeToUser() {
        TreeEntity tree = new TreeEntity();
        UserEntity user = new UserEntity();
        TreeResponse response = new TreeResponse();

        when(treeRepository.findById(1L)).thenReturn(Optional.of(tree));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(treeRepository.save(tree)).thenReturn(tree);
        when(treeMapper.toResponse(tree)).thenReturn(response);

        TreeResponse result = service.assignTreeToUser(1L, 2L);

        assertEquals(response, result);
        assertEquals(user, tree.getOwnerUser());
        verify(treeRepository).save(tree);
    }
}