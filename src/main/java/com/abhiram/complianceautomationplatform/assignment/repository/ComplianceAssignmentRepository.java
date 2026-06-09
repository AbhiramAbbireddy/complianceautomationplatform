package com.abhiram.complianceautomationplatform.assignment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhiram.complianceautomationplatform.assignment.entity.ComplianceAssignment;
import com.abhiram.complianceautomationplatform.common.enums.ComplianceStatus;
import com.abhiram.complianceautomationplatform.compliance.entity.Compliance;
import com.abhiram.complianceautomationplatform.user.entity.User;

public interface ComplianceAssignmentRepository
                extends JpaRepository<ComplianceAssignment, Long> {
        List<ComplianceAssignment> findByAssignedTo(
                        User assignedTo);

        List<ComplianceAssignment> findByAssignedBy(
                        User assignedBy);

        Optional<ComplianceAssignment> findByIdAndAssignedTo(
                        Long id,
                        User assignedTo);

        long countByAssignedTo(
                        User assignedTo);

        long countByAssignedToAndCompliance_Status(
                        User assignedTo,
                        ComplianceStatus status);

        boolean existsByComplianceAndAssignedTo(
                        Compliance compliance,
                        User assignedTo);

        Optional<ComplianceAssignment> findByIdAndAssignedBy(
                        Long id,
                        User assignedBy);

        List<ComplianceAssignment> findByCompliance_StatusNot(
                        ComplianceStatus status);
}
