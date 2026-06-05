package com.abhiram.complianceautomationplatform.compliance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhiram.complianceautomationplatform.compliance.entity.Compliance;

public interface ComplianceRepository extends JpaRepository<Compliance,Long> {
}