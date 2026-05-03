package com.forestplus.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.forestplus.dto.response.HomeDashboardKpiResponse;
import com.forestplus.dto.response.PlannedPlantationKpiResponse;
import com.forestplus.entity.PlannedPlantationEntity;
import com.forestplus.entity.TreeEntity;
import com.forestplus.repository.CompanyRepository;
import com.forestplus.repository.PlannedPlantationRepository;
import com.forestplus.repository.TreeRepository;
import com.forestplus.repository.UserRepository;
import com.forestplus.security.CurrentUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final TreeRepository treeRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PlannedPlantationRepository plannedPlantationRepository;
    private final CurrentUserService currentUserService;

    @Override
    public HomeDashboardKpiResponse getHomeKpis(Long userId) {
    	
        // 1️⃣ Obtener contexto del usuario actual mediante tu servicio
        String currentRole = currentUserService.getCurrentUserRole();
        Long currentCompanyId = currentUserService.getCurrentUserCompanyId();
        
        // Determinar si el contexto es de empresa
        boolean isCompanyRole = "COMPANY_ADMIN".equals(currentRole) || "COMPANY_USER".equals(currentRole);

        // 1️⃣ Empresas del usuario
    	List<Long> companyIdsTemp = companyRepository.findCompanyIdsByUserId(userId);
    	final List<Long> companyIds = companyIdsTemp.isEmpty() ? null : companyIdsTemp;

        // 2️⃣ KPI: árboles plantados (usuario + empresas)
        long plantedTrees = treeRepository.countOwnedTrees(userId, null);
        
        int pendingTreesCount = userRepository.findById(userId)
                .map(user -> user.getPendingTreesCount())
                .orElse(0);
        
        BigDecimal annualCo2Compensated =
                treeRepository.sumAnnualCo2At20(userId, null);

        // 3️⃣ Plantaciones activas
        List<PlannedPlantationEntity> activePlantations = plannedPlantationRepository.findAllByIsActiveTrue();
        
        if (isCompanyRole) {
            activePlantations = activePlantations.stream()
                .filter(pp -> pp.getLand() != null && 
                        pp.getLand().getCompanies() != null &&
                        pp.getLand().getCompanies().stream()
                            .anyMatch(c -> c.getId().equals(currentCompanyId)))
                .toList();
        }

        // 4️⃣ Transformar cada plantación en KPI
        List<PlannedPlantationKpiResponse> plantationKpis = activePlantations.stream()
                .<PlannedPlantationKpiResponse>map(pp -> {
                    List<TreeEntity> trees = treeRepository.findByLandId(pp.getLand().getId());
                    long soldTrees = trees.stream()
                            .count();
                    return PlannedPlantationKpiResponse.builder()
                            .plantationId(pp.getId())
                            .plantationName(pp.getLand() != null ? pp.getLand().getName() : "Sin terreno")
                            .minTrees(pp.getMinTrees())
                            .optimalTrees(pp.getOptimalTrees())
                            .maxTrees(pp.getMaxTrees())
                            .soldTrees(soldTrees)
                            .build();
                })
                .toList();

        // KPI Globales (Nuestro Proyecto)
        long globalPlantedTrees;
        BigDecimal globalAnnualCo2Compensated;
        if (isCompanyRole) {
            // Si es de empresa, los "Globales" son solo los de sus empresas
            globalPlantedTrees = treeRepository.countOwnedTrees(null, companyIds); // userId null para que solo cuente empresas
            globalAnnualCo2Compensated = treeRepository.sumAnnualCo2At20(null, companyIds);
        } else {
            // Si es un usuario normal (o admin total), ve los globales de toda la APP
            globalPlantedTrees = treeRepository.count();
            globalAnnualCo2Compensated = treeRepository.sumGlobalAnnualCo2At20();
        }

        // 5️⃣ Construir response
        return HomeDashboardKpiResponse.builder()
                .plantedTrees(plantedTrees)
                .pendingTreesCount(pendingTreesCount)
                .annualCo2Compensated(annualCo2Compensated)
                .globalPlantedTrees(globalPlantedTrees)
                .globalAnnualCo2Compensated(globalAnnualCo2Compensated)
                .plannedPlantations(plantationKpis)
                .build();
    }
}
