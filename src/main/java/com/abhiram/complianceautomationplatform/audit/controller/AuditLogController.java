package com.abhiram.complianceautomationplatform.audit.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abhiram.complianceautomationplatform.audit.dto.AuditLogResponse;
import com.abhiram.complianceautomationplatform.audit.service.AuditLogService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AuditLogController {
    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER','COMPLIANCE_MANAGER','AUDITOR')")
    public List<AuditLogResponse> getAllLogs() {
        return auditLogService.getAllLogs();
    }
}