package com.abhiram.complianceautomationplatform.user.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abhiram.complianceautomationplatform.department.entity.Department;
import com.abhiram.complianceautomationplatform.department.repository.DepartmentRepository;
import com.abhiram.complianceautomationplatform.exception.DuplicateResourceException;
import com.abhiram.complianceautomationplatform.exception.ResourceNotFoundException;
import com.abhiram.complianceautomationplatform.role.RoleConstants;
import com.abhiram.complianceautomationplatform.role.entity.Role;
import com.abhiram.complianceautomationplatform.role.repository.RoleRepository;
import com.abhiram.complianceautomationplatform.security.CustomUserPrincipal;
import com.abhiram.complianceautomationplatform.user.dto.CreateDepartmentManagerRequest;
import com.abhiram.complianceautomationplatform.user.dto.CreateEmployeeRequest;
import com.abhiram.complianceautomationplatform.user.dto.UserResponse;
import com.abhiram.complianceautomationplatform.user.entity.User;
import com.abhiram.complianceautomationplatform.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
        private final UserRepository userRepository;
        private final DepartmentRepository departmentRepository;
        private final RoleRepository roleRepository;
        private final PasswordEncoder passwordEncoder;

        @Transactional
        public UserResponse createDepartmentManager(
                        CreateDepartmentManagerRequest request,
                        Authentication authentication) {
                if (userRepository.existsByEmail(
                                request.getEmail())) {
                        throw new DuplicateResourceException(
                                        "Email already exists");
                }

                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User currentUser = principal.getUser();

                Department department = departmentRepository
                                .findByIdAndCompany(
                                                request.getDepartmentId(),
                                                currentUser.getCompany())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Department not found"));

                Role role = roleRepository.findByName(
                                RoleConstants.DEPARTMENT_MANAGER)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Role not found"));

                User user = User.builder()
                                .name(request.getName())
                                .email(request.getEmail())
                                .password(
                                                passwordEncoder.encode(
                                                                request.getPassword()))
                                .enabled(true)
                                .company(currentUser.getCompany())
                                .department(department)
                                .role(role)
                                .createdAt(
                                                java.time.LocalDateTime.now())
                                .build();

                user = userRepository.save(user);

                return mapToResponse(user);
        }

        @Transactional(readOnly=true)
        public List<UserResponse> getUsers(
                        Authentication authentication) {
                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User currentUser = principal.getUser();

                return userRepository
                                .findByCompany(
                                                currentUser.getCompany())
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

        @Transactional(readOnly=true)
        public UserResponse getUserById(
                        Long id,
                        Authentication authentication) {
                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User currentUser = principal.getUser();

                User user = userRepository.findByIdAndCompany(
                                id,
                                currentUser.getCompany())
                                .orElseThrow(() -> new RuntimeException(
                                                "User not found"));

                return mapToResponse(user);
        }

        private UserResponse mapToResponse(
                        User user) {
                return UserResponse.builder()
                                .id(user.getId())
                                .name(user.getName())
                                .email(user.getEmail())
                                .role(
                                                user.getRole().getName())
                                .department(
                                                user.getDepartment() != null
                                                                ? user.getDepartment().getName()
                                                                : null)
                                .manager(
                                                user.getManager() != null
                                                                ? user.getManager().getName()
                                                                : null)
                                .build();
        }

        @Transactional
        public UserResponse createEmployee(
                        CreateEmployeeRequest request,
                        Authentication authentication) {
                if (userRepository.existsByEmail(
                                request.getEmail())) {
                        throw new DuplicateResourceException(
                                        "Email already exists");
                }

                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User currentUser = principal.getUser();

                Department department = departmentRepository
                                .findByIdAndCompany(
                                                request.getDepartmentId(),
                                                currentUser.getCompany())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Department not found"));

                User manager = userRepository.findByIdAndCompany(
                                request.getManagerId(),
                                currentUser.getCompany())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Manager not found"));

                if (!manager.getRole()
                                .getName()
                                .equals(RoleConstants.DEPARTMENT_MANAGER)) {
                        throw new RuntimeException(
                                        "Selected user is not a Department Manager");
                }

                Role employeeRole = roleRepository.findByName(
                                RoleConstants.EMPLOYEE)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Employee role not found"));

                User employee = User.builder()
                                .name(request.getName())
                                .email(request.getEmail())
                                .password(
                                                passwordEncoder.encode(
                                                                request.getPassword()))
                                .enabled(true)
                                .company(currentUser.getCompany())
                                .department(department)
                                .manager(manager)
                                .role(employeeRole)
                                .createdAt(
                                                java.time.LocalDateTime.now())
                                .build();

                employee = userRepository.save(employee);

                return mapToResponse(employee);
        }

        @Transactional(readOnly=true)
        public List<UserResponse> getMyTeam(
                        Authentication authentication) {
                CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

                User currentUser = principal.getUser();

                return userRepository
                                .findByManager(currentUser)
                                .stream()
                                .map(this::mapToResponse)
                                .toList();
        }

}
