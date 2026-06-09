package com.abhiram.complianceautomationplatform.dashboard.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abhiram.complianceautomationplatform.assignment.repository.ComplianceAssignmentRepository;
import com.abhiram.complianceautomationplatform.common.enums.ComplianceStatus;
import com.abhiram.complianceautomationplatform.compliance.repository.ComplianceRepository;
import com.abhiram.complianceautomationplatform.dashboard.dto.EmployeeDashboardResponse;
import com.abhiram.complianceautomationplatform.dashboard.dto.ManagerDashboardResponse;
import com.abhiram.complianceautomationplatform.dashboard.dto.OwnerDashboardResponse;
import com.abhiram.complianceautomationplatform.dashboard.dto.TeamMemberPerformanceResponse;
import com.abhiram.complianceautomationplatform.department.repository.DepartmentRepository;
import com.abhiram.complianceautomationplatform.exception.ResourceNotFoundException;
import com.abhiram.complianceautomationplatform.role.RoleConstants;
import com.abhiram.complianceautomationplatform.role.entity.Role;
import com.abhiram.complianceautomationplatform.role.repository.RoleRepository;
import com.abhiram.complianceautomationplatform.security.CustomUserPrincipal;
import com.abhiram.complianceautomationplatform.user.entity.User;
import com.abhiram.complianceautomationplatform.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

        private final DepartmentRepository departmentRepository;

        private final UserRepository userRepository;

        private final ComplianceRepository complianceRepository;

        private final RoleRepository roleRepository;

        private final ComplianceAssignmentRepository assignmentRepository;

        @Transactional(readOnly = true)
        public OwnerDashboardResponse getOwnerDashboard(
                        Authentication authentication) {
                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User currentUser = principal.getUser();

                return OwnerDashboardResponse.builder()
                                .totalDepartments(
                                                departmentRepository.countByCompany(
                                                                currentUser.getCompany()))
                                .totalUsers(
                                                userRepository.countByCompany(
                                                                currentUser.getCompany()))
                                .totalCompliances(
                                                complianceRepository.countByCompany(
                                                                currentUser.getCompany()))
                                .pendingCompliances(
                                                complianceRepository
                                                                .countByCompanyAndStatus(
                                                                                currentUser.getCompany(),
                                                                                ComplianceStatus.PENDING))
                                .inProgressCompliances(
                                                complianceRepository
                                                                .countByCompanyAndStatus(
                                                                                currentUser.getCompany(),
                                                                                ComplianceStatus.IN_PROGRESS))
                                .completedCompliances(
                                                complianceRepository
                                                                .countByCompanyAndStatus(
                                                                                currentUser.getCompany(),
                                                                                ComplianceStatus.COMPLETED))
                                .verifiedCompliances(
                                                complianceRepository
                                                                .countByCompanyAndStatus(
                                                                                currentUser.getCompany(),
                                                                                ComplianceStatus.VERIFIED))

                                .overdueCompliances(
                                                complianceRepository
                                                                .countByCompanyAndDueDateBeforeAndStatusNot(
                                                                                currentUser.getCompany(),
                                                                                LocalDate.now(),
                                                                                ComplianceStatus.VERIFIED))
                                .build();
        }

        @Transactional(readOnly = true)
        public ManagerDashboardResponse getManagerDashboard(
                        Authentication authentication) {
                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User currentUser = userRepository
                                .findById(
                                                principal.getUser().getId())
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "User not found"));
                Role employeeRole = roleRepository
                                .findByName(
                                                RoleConstants.EMPLOYEE)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Role not found"));

                return ManagerDashboardResponse.builder()
                                .department(
                                                currentUser.getDepartment()
                                                                .getName())
                                .totalEmployees(
                                                userRepository.countByDepartmentAndRole(
                                                                currentUser.getDepartment(),
                                                                employeeRole))
                                .totalCompliances(
                                                complianceRepository.countByDepartment(
                                                                currentUser.getDepartment()))
                                .pendingCompliances(
                                                complianceRepository
                                                                .countByDepartmentAndStatus(
                                                                                currentUser.getDepartment(),
                                                                                ComplianceStatus.PENDING))
                                .inProgressCompliances(
                                                complianceRepository
                                                                .countByDepartmentAndStatus(
                                                                                currentUser.getDepartment(),
                                                                                ComplianceStatus.IN_PROGRESS))
                                .completedCompliances(
                                                complianceRepository
                                                                .countByDepartmentAndStatus(
                                                                                currentUser.getDepartment(),
                                                                                ComplianceStatus.COMPLETED))
                                .verifiedCompliances(
                                                complianceRepository
                                                                .countByDepartmentAndStatus(
                                                                                currentUser.getDepartment(),
                                                                                ComplianceStatus.VERIFIED))

                                .overdueCompliances(
                                                complianceRepository
                                                                .countByDepartmentAndDueDateBeforeAndStatusNot(
                                                                                currentUser.getDepartment(),
                                                                                LocalDate.now(),
                                                                                ComplianceStatus.VERIFIED))
                                .build();
        }

        @Transactional(readOnly = true)
        public List<TeamMemberPerformanceResponse> getTeamPerformance(
                        Authentication authentication) {
                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User currentUser = userRepository
                                .findByIdWithDepartment(
                                                principal.getUser().getId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found"));

                Role employeeRole = roleRepository
                                .findByName(
                                                RoleConstants.EMPLOYEE)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Role not found"));

                List<User> employees = userRepository
                                .findByDepartmentAndRole(
                                                currentUser.getDepartment(),
                                                employeeRole);

                return employees.stream()
                                .map(employee -> TeamMemberPerformanceResponse
                                                .builder()
                                                .userId(employee.getId())
                                                .employeeName(
                                                                employee.getName())
                                                .assignedTasks(
                                                                assignmentRepository
                                                                                .countByAssignedTo(
                                                                                                employee))
                                                .completedTasks(
                                                                assignmentRepository
                                                                                .countByAssignedToAndCompliance_Status(
                                                                                                employee,
                                                                                                ComplianceStatus.COMPLETED))
                                                .build())
                                .toList();
        }

        @Transactional(readOnly = true)
        public EmployeeDashboardResponse getEmployeeDashboard(
                        Authentication authentication) {

                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User currentUser = userRepository.findById(
                                principal.getUser().getId())
                                .orElseThrow(
                                                () -> new ResourceNotFoundException(
                                                                "User not found"));

                return EmployeeDashboardResponse.builder()
                                .assignedTasks(
                                                assignmentRepository.countByAssignedTo(
                                                                currentUser))
                                .pendingTasks(
                                                assignmentRepository
                                                                .countByAssignedToAndCompliance_Status(
                                                                                currentUser,
                                                                                ComplianceStatus.PENDING))
                                .inProgressTasks(
                                                assignmentRepository
                                                                .countByAssignedToAndCompliance_Status(
                                                                                currentUser,
                                                                                ComplianceStatus.IN_PROGRESS))
                                .completedTasks(
                                                assignmentRepository
                                                                .countByAssignedToAndCompliance_Status(
                                                                                currentUser,
                                                                                ComplianceStatus.COMPLETED))
                                .verifiedTasks(
                                                assignmentRepository
                                                                .countByAssignedToAndCompliance_Status(
                                                                                currentUser,
                                                                                ComplianceStatus.VERIFIED))
                                .build();
        }

}
