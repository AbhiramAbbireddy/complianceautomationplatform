package com.abhiram.complianceautomationplatform.assignment.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abhiram.complianceautomationplatform.assignment.dto.AssignmentResponse;
import com.abhiram.complianceautomationplatform.assignment.dto.CreateAssignmentRequest;
import com.abhiram.complianceautomationplatform.assignment.dto.UpdateAssignmentStatusRequest;
import com.abhiram.complianceautomationplatform.assignment.entity.ComplianceAssignment;
import com.abhiram.complianceautomationplatform.assignment.repository.ComplianceAssignmentRepository;
import com.abhiram.complianceautomationplatform.audit.annotation.Audit;
import com.abhiram.complianceautomationplatform.audit.service.AuditLogService;
import com.abhiram.complianceautomationplatform.common.enums.ComplianceStatus;
import com.abhiram.complianceautomationplatform.compliance.entity.Compliance;
import com.abhiram.complianceautomationplatform.compliance.repository.ComplianceRepository;
import com.abhiram.complianceautomationplatform.document.repository.ComplianceDocumentRepository;
import com.abhiram.complianceautomationplatform.exception.BusinessException;
import com.abhiram.complianceautomationplatform.exception.ResourceNotFoundException;
import com.abhiram.complianceautomationplatform.notification.service.EmailService;
import com.abhiram.complianceautomationplatform.role.RoleConstants;
import com.abhiram.complianceautomationplatform.security.CustomUserPrincipal;
import com.abhiram.complianceautomationplatform.user.entity.User;
import com.abhiram.complianceautomationplatform.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssignmentService {
        private final ComplianceAssignmentRepository assignmentRepository;
        private final ComplianceRepository complianceRepository;
        private final UserRepository userRepository;
        private final AuditLogService auditLogService;
        private final ComplianceDocumentRepository documentRepository;
        private final EmailService emailService;

        @Audit(action = "ASSIGN_COMPLIANCE", entityType = "COMPLIANCE")
        @Transactional
        public AssignmentResponse createAssignment(
                        CreateAssignmentRequest request,
                        Authentication authentication) {
                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User currentUser = principal.getUser();

                Compliance compliance = complianceRepository
                                .findByIdAndCompany(
                                                request.getComplianceId(),
                                                currentUser.getCompany())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Compliance not found"));

                User employee = userRepository
                                .findByIdAndCompany(
                                                request.getEmployeeId(),
                                                currentUser.getCompany())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Employee not found"));

                if (!employee.getRole()
                                .getName()
                                .equals(RoleConstants.EMPLOYEE)) {
                        throw new RuntimeException(
                                        "Selected user is not an employee");
                }

                if (!employee.getDepartment()
                                .getId()
                                .equals(
                                                compliance.getDepartment()
                                                                .getId())) {
                        throw new RuntimeException(
                                        "Employee and Compliance belong to different departments");
                }

                ComplianceAssignment assignment = ComplianceAssignment.builder()
                                .compliance(compliance)
                                .assignedTo(employee)
                                .assignedBy(currentUser)
                                .assignedAt(
                                                java.time.LocalDateTime.now())
                                .build();

                assignment = assignmentRepository.save(
                                assignment);

                emailService.sendAssignmentNotification(
                                employee.getEmail(),
                                employee.getName(),
                                compliance.getTitle(),
                                currentUser.getName());

                auditLogService.log(
                                "ASSIGN_COMPLIANCE",
                                "COMPLIANCE",
                                compliance.getId(),
                                currentUser.getEmail(),
                                "Assigned "
                                                + compliance.getTitle()
                                                + " to "
                                                + employee.getName());

                return mapToResponse(
                                assignment);
        }

        @Transactional(readOnly = true)
        public List<AssignmentResponse> getMyTasks(
                        Authentication authentication) {
                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User currentUser = principal.getUser();

                return assignmentRepository
                                .findByAssignedTo(
                                                currentUser)
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        private AssignmentResponse mapToResponse(
                        ComplianceAssignment assignment) {
                return AssignmentResponse.builder()
                                .id(
                                                assignment.getId())
                                .compliance(
                                                assignment.getCompliance()
                                                                .getTitle())
                                .assignedTo(
                                                assignment.getAssignedTo()
                                                                .getName())
                                .assignedBy(
                                                assignment.getAssignedBy()
                                                                .getName())
                                .assignedAt(
                                                assignment.getAssignedAt())
                                .remarks(
                                                assignment.getRemarks())
                                .completedAt(
                                                assignment.getCompletedAt())
                                .status(
                                                assignment.getCompliance()
                                                                .getStatus()
                                                                .name())
                                .build();
        }

        @Transactional
        public AssignmentResponse updateStatus(
                        Long assignmentId,
                        UpdateAssignmentStatusRequest request,
                        Authentication authentication) {
                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User currentUser = principal.getUser();

                ComplianceAssignment assignment = assignmentRepository
                                .findByIdAndAssignedTo(
                                                assignmentId,
                                                currentUser)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Assignment not found"));

                assignment.setRemarks(
                                request.getRemarks());

                assignment.getCompliance()
                                .setStatus(
                                                request.getStatus());

                if (request.getStatus() == ComplianceStatus.COMPLETED) {
                        assignment.setCompletedAt(
                                        java.time.LocalDateTime.now());
                        emailService.sendCompletionNotification(
                                        assignment.getAssignedBy().getEmail(),
                                        assignment.getAssignedBy().getName(),
                                        assignment.getCompliance().getTitle(),
                                        currentUser.getName());
                }

                assignment = assignmentRepository.save(
                                assignment);

                auditLogService.log(
                                "UPDATE_STATUS",
                                "COMPLIANCE",
                                assignment.getCompliance().getId(),
                                currentUser.getEmail(),
                                "Status changed to "
                                                + request.getStatus());

                return mapToResponse(
                                assignment);
        }

        @Transactional
        public AssignmentResponse verifyAssignment(
                        Long assignmentId,
                        Authentication authentication) {

                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User currentUser = principal.getUser();

                ComplianceAssignment assignment = assignmentRepository
                                .findByIdAndAssignedBy(
                                                assignmentId,
                                                currentUser)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Assignment not found"));

                Compliance compliance = assignment.getCompliance();

                if (compliance.getStatus() != ComplianceStatus.COMPLETED) {

                        throw new RuntimeException(
                                        "Only completed compliances can be verified");
                }

                if (documentRepository
                                .findByCompliance(compliance)
                                .isEmpty()) {

                        throw new BusinessException(
                                        "No supporting documents uploaded for "
                                                        + compliance.getTitle());
                }

                compliance.setStatus(
                                ComplianceStatus.VERIFIED);

                emailService.sendVerificationNotification(
                                assignment.getAssignedTo().getEmail(),
                                assignment.getAssignedTo().getName(),
                                compliance.getTitle());

                auditLogService.log(
                                "VERIFY_COMPLIANCE",
                                "COMPLIANCE",
                                compliance.getId(),
                                currentUser.getEmail(),
                                "Verified " + compliance.getTitle());

                return mapToResponse(
                                assignment);
        }
}
