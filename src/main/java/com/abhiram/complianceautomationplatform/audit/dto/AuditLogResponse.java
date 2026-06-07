package com.abhiram.complianceautomationplatform.audit.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuditLogResponse {
    private Long id;

    private String action;

    private String entityType;

    private Long entityId;

    private String performedBy;

    private LocalDateTime performedAt;

    private String details;
}