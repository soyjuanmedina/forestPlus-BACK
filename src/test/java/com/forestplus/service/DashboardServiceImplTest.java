package com.forestplus.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.forestplus.dto.response.HomeDashboardKpiResponse;
import com.forestplus.entity.LandEntity;
import com.forestplus.entity.PlannedPlantationEntity;
import com.forestplus.entity.TreeEntity;
import com.forestplus.entity.UserEntity;
import com.forestplus.repository.CompanyRepository;
import com.forestplus.repository.PlannedPlantationRepository;
import com.forestplus.repository.TreeRepository;
import com.forestplus.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private TreeRepository treeRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlannedPlantationRepository plannedPlantationRepository;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private final Long USER_ID = 1L;

    @BeforeEach
    void setup() {
    }

    // ---------------------------------------------------------
    // HAPPY PATH COMPLETO
    // ---------------------------------------------------------
    @Test
    void shouldReturnHomeKpis() {

        // Companies
        when(companyRepository.findCompanyIdsByUserId(USER_ID))
                .thenReturn(List.of(10L, 20L));

        // Trees
        when(treeRepository.countOwnedTrees(USER_ID, List.of(10L, 20L)))
                .thenReturn(5L);

        when(treeRepository.sumAnnualCo2At20(USER_ID, List.of(10L, 20L)))
                .thenReturn(BigDecimal.TEN);

        // User
        UserEntity user = new UserEntity();
        user.setPendingTreesCount(3);

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        // Plantation
        LandEntity land = new LandEntity();
        land.setId(100L);
        land.setName("Amazonas");

        PlannedPlantationEntity plantation = new PlannedPlantationEntity();
        plantation.setId(1L);
        plantation.setMinTrees(50);
        plantation.setLand(land);

        when(plannedPlantationRepository.findAllByIsActiveTrue())
                .thenReturn(List.of(plantation));

        when(treeRepository.findByLandId(100L))
                .thenReturn(List.of(new TreeEntity(), new TreeEntity()));

        HomeDashboardKpiResponse response =
                dashboardService.getHomeKpis(USER_ID);

        assertEquals(5L, response.getPlantedTrees());
        assertEquals(3, response.getPendingTreesCount());
        assertEquals(BigDecimal.TEN, response.getAnnualCo2Compensated());
        assertEquals(1, response.getPlannedPlantations().size());
        assertEquals(2L, response.getPlannedPlantations().get(0).getSoldTrees());
    }

    // ---------------------------------------------------------
    // WHEN USER HAS NO COMPANIES
    // ---------------------------------------------------------
    @Test
    void shouldHandleEmptyCompanyList() {

        when(companyRepository.findCompanyIdsByUserId(USER_ID))
                .thenReturn(List.of());

        when(treeRepository.countOwnedTrees(USER_ID, null))
                .thenReturn(0L);

        when(treeRepository.sumAnnualCo2At20(USER_ID, null))
                .thenReturn(BigDecimal.ZERO);

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        when(plannedPlantationRepository.findAllByIsActiveTrue())
                .thenReturn(List.of());

        HomeDashboardKpiResponse response =
                dashboardService.getHomeKpis(USER_ID);

        assertEquals(0L, response.getPlantedTrees());
        assertEquals(0, response.getPendingTreesCount());
        assertEquals(BigDecimal.ZERO, response.getAnnualCo2Compensated());
        assertTrue(response.getPlannedPlantations().isEmpty());
    }

    // ---------------------------------------------------------
    // PLANTATION WITHOUT LAND
    // ---------------------------------------------------------
    @Test
    void shouldHandlePlantationWithoutLand() {

        when(companyRepository.findCompanyIdsByUserId(USER_ID))
                .thenReturn(List.of());

        when(treeRepository.countOwnedTrees(USER_ID, null))
                .thenReturn(0L);

        when(treeRepository.sumAnnualCo2At20(USER_ID, null))
                .thenReturn(BigDecimal.ZERO);

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        PlannedPlantationEntity plantation = new PlannedPlantationEntity();
        plantation.setId(99L);
        plantation.setMinTrees(10);
        LandEntity land = new LandEntity();
        land.setId(123L);
        land.setName("Terreno Test");
        plantation.setLand(land);

        when(plannedPlantationRepository.findAllByIsActiveTrue())
                .thenReturn(List.of(plantation));

        HomeDashboardKpiResponse response =
                dashboardService.getHomeKpis(USER_ID);

        assertEquals("Terreno Test",
                response.getPlannedPlantations().get(0).getPlantationName());
    }
}