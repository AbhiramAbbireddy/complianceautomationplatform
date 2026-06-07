package com.abhiram.complianceautomationplatform.assignment.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AssignmentResponse {
    private Long id;

    private String compliance;

    private String assignedTo;

    private String assignedBy;

    private LocalDateTime assignedAt;

    private String remarks;

    private LocalDateTime completedAt;

    private String status;
}
