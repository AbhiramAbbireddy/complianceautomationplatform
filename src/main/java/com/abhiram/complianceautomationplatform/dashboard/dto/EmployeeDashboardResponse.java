package com.abhiram.complianceautomationplatform.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmployeeDashboardResponse {

    private long assignedTasks;

    private long pendingTasks;

    private long inProgressTasks;

    private long completedTasks;

    private long verifiedTasks;
}