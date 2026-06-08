package com.abhiram.complianceautomationplatform.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhiram.complianceautomationplatform.compliance.entity.Compliance;
import com.abhiram.complianceautomationplatform.document.entity.ComplianceDocument;

public interface ComplianceDocumentRepository
        extends JpaRepository<ComplianceDocument, Long> {
    List<ComplianceDocument> findByCompliance(
            Compliance compliance);
}
