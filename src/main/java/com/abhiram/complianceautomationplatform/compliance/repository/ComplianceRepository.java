package com.abhiram.complianceautomationplatform.compliance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhiram.complianceautomationplatform.common.enums.ComplianceStatus;
import com.abhiram.complianceautomationplatform.company.entity.Company;
import com.abhiram.complianceautomationplatform.compliance.entity.Compliance;
import com.abhiram.complianceautomationplatform.department.entity.Department;

public interface ComplianceRepository extends JpaRepository<Compliance, Long> {
        List<Compliance> findByCompany(Company company);

        Optional<Compliance> findByIdAndCompany(
                        Long id,
                        Company company);

        List<Compliance> findByDepartment(
                        Department department);

        long countByCompany(Company company);

        long countByCompanyAndStatus(
                        Company company,
                        ComplianceStatus status);

        long countByDepartment(
                        Department department);

        long countByDepartmentAndStatus(
                        Department department,
                        ComplianceStatus status);
}