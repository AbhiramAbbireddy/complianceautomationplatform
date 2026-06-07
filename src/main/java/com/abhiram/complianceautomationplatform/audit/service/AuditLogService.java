package com.abhiram.complianceautomationplatform.audit.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abhiram.complianceautomationplatform.audit.dto.AuditLogResponse;
import com.abhiram.complianceautomationplatform.audit.entity.AuditLog;
import com.abhiram.complianceautomationplatform.audit.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void log(
            String action,
            String entityType,
            Long entityId,
            String performedBy,
            String details) {
        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .performedBy(performedBy)
                .performedAt(
                        LocalDateTime.now())
                .details(details)
                .build();

        auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAllLogs() {
        return auditLogRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AuditLogResponse mapToResponse(
            AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .action(auditLog.getAction())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .performedBy(auditLog.getPerformedBy())
                .performedAt(auditLog.getPerformedAt())
                .details(auditLog.getDetails())
                .build();
    }
}
