package com.forestplus.service;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final TreeRepository treeRepository;
    private final CompanyRepository companyRepository;
    private final PlannedPlantationRepository plannedPlantationRepository;

    @Override
    public HomeDashboardKpiResponse getHomeKpis(Long userId) {

        // 1️⃣ Empresas del usuario
    	List<Long> companyIdsTemp = companyRepository.findCompanyIdsByUserId(userId);
    	final List<Long> companyIds = companyIdsTemp.isEmpty() ? null : companyIdsTemp;

        // 2️⃣ KPI: árboles plantados (usuario + empresas)
        long plantedTrees = treeRepository.countOwnedTrees(userId, companyIds);

        // 3️⃣ Plantaciones activas
        List<PlannedPlantationEntity> activePlantations = plannedPlantationRepository.findAllByIsActiveTrue();

        // 4️⃣ Transformar cada plantación en KPI
        List<PlannedPlantationKpiResponse> plantationKpis = activePlantations.stream()
                .<PlannedPlantationKpiResponse>map(pp -> {
                    List<TreeEntity> trees = treeRepository.findByLandId(pp.getLand().getId());
                    long soldTrees = trees.stream()
                            .filter(t -> (t.getOwnerUser() != null && t.getOwnerUser().getId().equals(userId))
                                    || (t.getOwnerCompany() != null && companyIds != null && companyIds.contains(t.getOwnerCompany().getId())))
                            .count();
                    return PlannedPlantationKpiResponse.builder()
                            .plantationId(pp.getId())
                            .plantationName(pp.getLand() != null ? pp.getLand().getName() : "Sin terreno")
                            .minTrees(pp.getMinTrees())
                            .soldTrees(soldTrees)
                            .build();
                })
                .toList();

        // 5️⃣ Construir response
        return HomeDashboardKpiResponse.builder()
                .plantedTrees(plantedTrees)
                .plannedPlantations(plantationKpis)
                .build();
    }
}
