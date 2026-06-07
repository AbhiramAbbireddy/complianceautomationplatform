package com.abhiram.complianceautomationplatform.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OwnerDashboardResponse {
    private long totalDepartments;

    private long totalUsers;

    private long totalCompliances;

    private long pendingCompliances;

    private long inProgressCompliances;

    private long completedCompliances;
}
