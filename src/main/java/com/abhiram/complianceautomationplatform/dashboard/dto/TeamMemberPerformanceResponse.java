package com.abhiram.complianceautomationplatform.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamMemberPerformanceResponse {
    private Long userId;

    private String employeeName;

    private long assignedTasks;

    private long completedTasks;
}
