package com.abhiram.complianceautomationplatform.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhiram.complianceautomationplatform.assignment.entity.ComplianceAssignment;

public interface ComplianceAssignmentRepository extends JpaRepository<ComplianceAssignment,Long> {
}
