package com.abhiram.complianceautomationplatform.compliance.dto;

import java.time.LocalDate;

import com.abhiram.complianceautomationplatform.common.enums.ComplianceFrequency;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateComplianceRequest
{
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    private ComplianceFrequency frequency;
}