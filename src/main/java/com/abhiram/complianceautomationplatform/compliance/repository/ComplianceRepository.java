package com.abhiram.complianceautomationplatform.compliance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhiram.complianceautomationplatform.company.entity.Company;
import com.abhiram.complianceautomationplatform.compliance.entity.Compliance;

public interface ComplianceRepository extends JpaRepository<Compliance, Long> {
    List<Compliance> findByCompany(Company company);

    Optional<Compliance> findByIdAndCompany(
            Long id,
            Company company);
}