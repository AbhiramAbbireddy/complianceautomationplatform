package com.abhiram.complianceautomationplatform.department.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.abhiram.complianceautomationplatform.audit.annotation.Audit;
import com.abhiram.complianceautomationplatform.department.dto.CreateDepartmentRequest;
import com.abhiram.complianceautomationplatform.department.dto.DepartmentResponse;
import com.abhiram.complianceautomationplatform.department.entity.Department;
import com.abhiram.complianceautomationplatform.department.repository.DepartmentRepository;
import com.abhiram.complianceautomationplatform.security.CustomUserPrincipal;
import com.abhiram.complianceautomationplatform.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    @Audit(
        action = "CREATE_DEPARTMENT",
        entityType = "DEPARTMENT",
        details = "Department created")
    public DepartmentResponse createDepartment(
            CreateDepartmentRequest request,
            Authentication authentication) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        User currentUser = principal.getUser();

        Department department = Department.builder()
                .name(request.getName())
                .company(currentUser.getCompany())
                .createdAt(LocalDateTime.now())
                .build();

        department = departmentRepository.save(department);

        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .build();
    }

    public List<DepartmentResponse> getDepartments(
            Authentication authentication) {
        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        User currentUser = principal.getUser();

        return departmentRepository
                .findByCompany(currentUser.getCompany())
                .stream()
                .map(department -> DepartmentResponse.builder()
                        .id(department.getId())
                        .name(department.getName())
                        .build())
                .collect(Collectors.toList());
    }
}
