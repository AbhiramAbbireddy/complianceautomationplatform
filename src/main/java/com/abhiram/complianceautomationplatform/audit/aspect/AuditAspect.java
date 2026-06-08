package com.abhiram.complianceautomationplatform.audit.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.abhiram.complianceautomationplatform.audit.annotation.Audit;
import com.abhiram.complianceautomationplatform.audit.service.AuditLogService;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {
    private final AuditLogService auditLogService;

    @AfterReturning(value = "@annotation(audit)")
    public void logAudit(
            JoinPoint joinPoint,
            Audit audit) {
        auditLogService.log(
                audit.action(),
                audit.entityType(),
                null,
                "SYSTEM",
                "Triggered by AOP");
    }
}
