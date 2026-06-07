package com.abhiram.complianceautomationplatform.dashboard.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abhiram.complianceautomationplatform.dashboard.dto.ManagerDashboardResponse;
import com.abhiram.complianceautomationplatform.dashboard.dto.OwnerDashboardResponse;
import com.abhiram.complianceautomationplatform.dashboard.dto.TeamMemberPerformanceResponse;
import com.abhiram.complianceautomationplatform.dashboard.service.DashboardService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {
        private final DashboardService dashboardService;

        @GetMapping("/owner")
        @PreAuthorize("hasRole('OWNER')")
        public OwnerDashboardResponse getOwnerDashboard(
                        Authentication authentication) {
                return dashboardService.getOwnerDashboard(
                                authentication);
        }

        @GetMapping("/manager")
        @PreAuthorize("hasRole('DEPARTMENT_MANAGER')")
        public ManagerDashboardResponse getManagerDashboard(
                        Authentication authentication) {
                return dashboardService
                                .getManagerDashboard(
                                                authentication);
        }

        @GetMapping("/manager/team")
        @PreAuthorize("hasRole('DEPARTMENT_MANAGER')")
        public List<TeamMemberPerformanceResponse> getTeamPerformance(
                        Authentication authentication) {
                return dashboardService
                                .getTeamPerformance(
                                                authentication);
        }
}