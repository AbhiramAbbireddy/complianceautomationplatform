package com.abhiram.complianceautomationplatform.company.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhiram.complianceautomationplatform.company.entity.Company;

public interface CompanyRepository extends JpaRepository<Company,Long> {
}
