package com.abhiram.complianceautomationplatform.audit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhiram.complianceautomationplatform.audit.entity.AuditLog;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByEntityTypeOrderByPerformedAtDesc(
            String entityType);

    List<AuditLog> findByPerformedByOrderByPerformedAtDesc(
            String performedBy);
}
