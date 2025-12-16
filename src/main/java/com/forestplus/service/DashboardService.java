package com.forestplus.service;

import com.forestplus.dto.response.HomeDashboardKpiResponse;

public interface DashboardService {

    HomeDashboardKpiResponse getHomeKpis(Long userId);

}
