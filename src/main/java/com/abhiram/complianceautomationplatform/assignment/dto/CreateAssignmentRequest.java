package com.abhiram.complianceautomationplatform.assignment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAssignmentRequest {
    @NotNull
    private Long complianceId;

    @NotNull
    private Long employeeId;
}