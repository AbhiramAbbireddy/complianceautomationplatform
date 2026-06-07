package com.abhiram.complianceautomationplatform.compliance.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abhiram.complianceautomationplatform.compliance.dto.ComplianceResponse;
import com.abhiram.complianceautomationplatform.compliance.dto.CreateComplianceRequest;
import com.abhiram.complianceautomationplatform.compliance.dto.UpdateComplianceRequest;
import com.abhiram.complianceautomationplatform.compliance.service.ComplianceService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/compliances")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ComplianceController {
        private final ComplianceService complianceService;

        @PostMapping
        @PreAuthorize("hasAnyRole('OWNER','COMPLIANCE_MANAGER')")
        public ComplianceResponse createCompliance(
                        @Valid @RequestBody CreateComplianceRequest request,
                        Authentication authentication) {
                return complianceService.createCompliance(
                                request,
                                authentication);
        }

        @GetMapping
        @PreAuthorize("hasAnyRole('OWNER','COMPLIANCE_MANAGER','AUDITOR')")
        public List<ComplianceResponse> getAllCompliances(
                        Authentication authentication) {
                return complianceService.getAllCompliances(
                                authentication);
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('OWNER','COMPLIANCE_MANAGER','AUDITOR')")
        public ComplianceResponse getComplianceById(
                        @PathVariable Long id,
                        Authentication authentication) {
                return complianceService.getComplianceById(
                                id,
                                authentication);
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasAnyRole('OWNER','COMPLIANCE_MANAGER')")
        public ComplianceResponse updateCompliance(
                        @PathVariable Long id,
                        @Valid @RequestBody UpdateComplianceRequest request,
                        Authentication authentication) {
                return complianceService.updateCompliance(
                                id,
                                request,
                                authentication);
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasAnyRole('OWNER','COMPLIANCE_MANAGER')")
        public String deleteCompliance(
                        @PathVariable Long id,
                        Authentication authentication) {
                complianceService.deleteCompliance(
                                id,
                                authentication);

                return "Compliance Deleted Successfully";
        }

        @GetMapping("/my-department")
        @PreAuthorize("hasRole('DEPARTMENT_MANAGER')")
        public List<ComplianceResponse> getMyDepartmentCompliances(
                        Authentication authentication) {
                return complianceService
                                .getMyDepartmentCompliances(
                                                authentication);
        }
}
