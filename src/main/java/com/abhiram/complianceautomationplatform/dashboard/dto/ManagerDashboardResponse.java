package com.abhiram.complianceautomationplatform.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ManagerDashboardResponse {
    private String department;

    private long totalEmployees;

    private long totalCompliances;

    private long pendingCompliances;

    private long inProgressCompliances;

    private long completedCompliances;
}