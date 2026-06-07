package com.abhiram.complianceautomationplatform.department.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abhiram.complianceautomationplatform.department.dto.CreateDepartmentRequest;
import com.abhiram.complianceautomationplatform.department.dto.DepartmentResponse;
import com.abhiram.complianceautomationplatform.department.service.DepartmentService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DepartmentController {
    private final DepartmentService departmentService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public DepartmentResponse createDepartment(
            @Valid @RequestBody CreateDepartmentRequest request,
            Authentication authentication) {
        return departmentService.createDepartment(
                request,
                authentication);
    }

    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    public List<DepartmentResponse> getDepartments(
            Authentication authentication) {
        return departmentService.getDepartments(
                authentication);
    }
}