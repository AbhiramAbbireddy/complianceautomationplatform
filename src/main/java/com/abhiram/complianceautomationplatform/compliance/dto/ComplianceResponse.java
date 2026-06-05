package com.abhiram.complianceautomationplatform.compliance.dto;

import java.time.LocalDate;

import com.abhiram.complianceautomationplatform.common.enums.ComplianceFrequency;
import com.abhiram.complianceautomationplatform.common.enums.ComplianceStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComplianceResponse {
    private Long id;

    private String title;

    private String description;

    private LocalDate dueDate;

    private ComplianceFrequency frequency;

    private ComplianceStatus status;
}