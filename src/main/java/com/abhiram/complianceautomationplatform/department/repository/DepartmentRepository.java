package com.abhiram.complianceautomationplatform.department.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhiram.complianceautomationplatform.company.entity.Company;
import com.abhiram.complianceautomationplatform.department.entity.Department;

public interface DepartmentRepository
                extends JpaRepository<Department, Long> {
        List<Department> findByCompany(Company company);

        Optional<Department> findByIdAndCompany(
                        Long id,
                        Company company);

        long countByCompany(Company company);
}
