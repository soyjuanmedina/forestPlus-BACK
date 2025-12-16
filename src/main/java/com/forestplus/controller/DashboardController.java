package com.forestplus.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.forestplus.dto.response.HomeDashboardKpiResponse;
import com.forestplus.security.CurrentUserService;
import com.forestplus.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final CurrentUserService currentUserService;

    @GetMapping(value = "/home", produces = "application/json")
    public HomeDashboardKpiResponse getHomeKpis() {
        Long userId = currentUserService.getCurrentUserId();
        return dashboardService.getHomeKpis(userId);
    }
}
