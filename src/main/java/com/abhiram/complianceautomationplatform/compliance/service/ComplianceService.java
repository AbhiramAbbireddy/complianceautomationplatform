package com.abhiram.complianceautomationplatform.compliance.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abhiram.complianceautomationplatform.assignment.repository.ComplianceAssignmentRepository;
import com.abhiram.complianceautomationplatform.audit.annotation.Audit;
import com.abhiram.complianceautomationplatform.common.enums.ComplianceStatus;
import com.abhiram.complianceautomationplatform.compliance.dto.ComplianceResponse;
import com.abhiram.complianceautomationplatform.compliance.dto.CreateComplianceRequest;
import com.abhiram.complianceautomationplatform.compliance.dto.UpdateComplianceRequest;
import com.abhiram.complianceautomationplatform.compliance.entity.Compliance;
import com.abhiram.complianceautomationplatform.compliance.repository.ComplianceRepository;
import com.abhiram.complianceautomationplatform.department.entity.Department;
import com.abhiram.complianceautomationplatform.department.repository.DepartmentRepository;
import com.abhiram.complianceautomationplatform.exception.ResourceNotFoundException;
import com.abhiram.complianceautomationplatform.security.CustomUserPrincipal;
import com.abhiram.complianceautomationplatform.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComplianceService {
        private final ComplianceRepository complianceRepository;

        private final DepartmentRepository departmentRepository;

        private final ComplianceAssignmentRepository complianceAssignmentRepository;

        @Audit(
        action = "CREATE_COMPLIANCE",
        entityType = "COMPLIANCE",
        details = "Compliance created")
        @Transactional
        public ComplianceResponse createCompliance(
                        CreateComplianceRequest request,
                        Authentication authentication) {
                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User user = principal.getUser();

                Department department = departmentRepository
                                .findByIdAndCompany(
                                                request.getDepartmentId(),
                                                user.getCompany())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Department not found"));

                Compliance compliance = Compliance.builder()
                                .title(request.getTitle())
                                .description(request.getDescription())
                                .dueDate(request.getDueDate())
                                .frequency(request.getFrequency())
                                .status(ComplianceStatus.PENDING)
                                .company(user.getCompany())
                                .department(department)
                                .createdBy(user)
                                .createdAt(LocalDateTime.now())
                                .build();

                complianceRepository.save(compliance);

                return ComplianceResponse.builder()
                                .id(compliance.getId())
                                .title(compliance.getTitle())
                                .description(compliance.getDescription())
                                .dueDate(compliance.getDueDate())
                                .frequency(compliance.getFrequency())
                                .status(compliance.getStatus())
                                .department(department.getName())
                                .build();
        }

        @Transactional(readOnly = true)
        public List<ComplianceResponse> getAllCompliances(
                        Authentication authentication) {
                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User currentUser = principal.getUser();

                List<Compliance> compliances = complianceRepository.findByCompany(
                                currentUser.getCompany());

                return compliances.stream()
                                .map(this::mapToResponse)
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public ComplianceResponse getComplianceById(
                        Long id,
                        Authentication authentication) {
                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User currentUser = principal.getUser();

                Compliance compliance = complianceRepository.findByIdAndCompany(
                                id,
                                currentUser.getCompany())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Compliance not found"));

                return mapToResponse(compliance);
        }

        private ComplianceResponse mapToResponse(Compliance compliance) {
                return ComplianceResponse.builder()
                                .id(compliance.getId())
                                .title(compliance.getTitle())
                                .description(compliance.getDescription())
                                .dueDate(compliance.getDueDate())
                                .frequency(compliance.getFrequency())
                                .status(compliance.getStatus())
                                .department(
                                                compliance.getDepartment() != null
                                                                ? compliance.getDepartment().getName()
                                                                : null)
                                .build();
        }

        @Transactional
        public ComplianceResponse updateCompliance(
                        Long id,
                        UpdateComplianceRequest request,
                        Authentication authentication) {
                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User currentUser = principal.getUser();

                Compliance compliance = complianceRepository.findByIdAndCompany(
                                id,
                                currentUser.getCompany())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Compliance not found"));

                compliance.setTitle(request.getTitle());
                compliance.setDescription(request.getDescription());
                compliance.setDueDate(request.getDueDate());
                compliance.setFrequency(request.getFrequency());

                compliance = complianceRepository.save(compliance);

                return mapToResponse(compliance);
        }

        @Transactional
        public void deleteCompliance(
                        Long id,
                        Authentication authentication) {
                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User currentUser = principal.getUser();

                Compliance compliance = complianceRepository.findByIdAndCompany(
                                id,
                                currentUser.getCompany())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Compliance not found"));

                complianceRepository.delete(compliance);
        }

        @Transactional(readOnly = true)
        public List<ComplianceResponse> getMyDepartmentCompliances(
                        Authentication authentication) {
                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User currentUser = principal.getUser();

                return complianceRepository
                                .findByDepartment(
                                                currentUser.getDepartment())
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        @Transactional(readOnly = true)
        public List<ComplianceResponse> getMyCompliances(
                        Authentication authentication) {
                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User currentUser = principal.getUser();

                return complianceAssignmentRepository
                                .findByAssignedTo(
                                                currentUser)
                                .stream()
                                .map(assignment -> mapToResponse(
                                                assignment.getCompliance()))
                                .toList();
        }
}
