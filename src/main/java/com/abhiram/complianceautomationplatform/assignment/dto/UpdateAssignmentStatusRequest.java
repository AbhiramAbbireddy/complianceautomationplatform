package com.abhiram.complianceautomationplatform.assignment.dto;

import com.abhiram.complianceautomationplatform.common.enums.ComplianceStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAssignmentStatusRequest {
    @NotNull
    private ComplianceStatus status;

    private String remarks;
}