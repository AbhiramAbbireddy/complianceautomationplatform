package com.abhiram.complianceautomationplatform.audit.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.abhiram.complianceautomationplatform.audit.annotation.Audit;
import com.abhiram.complianceautomationplatform.audit.service.AuditLogService;
import com.abhiram.complianceautomationplatform.security.SecurityUtils;

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

        String performedBy = SecurityUtils.getCurrentUserEmail();

        String details = audit.details().isBlank()
                ? java.util.Arrays.toString(
                        joinPoint.getArgs())
                : audit.details();

        auditLogService.log(
                audit.action(),
                audit.entityType(),
                null,
                performedBy,
                details);
    }
}
