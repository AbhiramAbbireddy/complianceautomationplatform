package com.abhiram.complianceautomationplatform.assignment.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abhiram.complianceautomationplatform.assignment.dto.AssignmentResponse;
import com.abhiram.complianceautomationplatform.assignment.dto.CreateAssignmentRequest;
import com.abhiram.complianceautomationplatform.assignment.dto.UpdateAssignmentStatusRequest;
import com.abhiram.complianceautomationplatform.assignment.service.AssignmentService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AssignmentController {
    private final AssignmentService assignmentService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER') or hasRole('DEPARTMENT_MANAGER')")
    public AssignmentResponse createAssignment(
            @Valid @RequestBody CreateAssignmentRequest request,

            Authentication authentication) {
        return assignmentService
                .createAssignment(
                        request,
                        authentication);
    }

    @GetMapping("/my-tasks")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<AssignmentResponse> getMyTasks(
            Authentication authentication) {
        return assignmentService
                .getMyTasks(
                        authentication);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public AssignmentResponse updateStatus(
            @PathVariable Long id,

            @Valid @RequestBody UpdateAssignmentStatusRequest request,

            Authentication authentication) {
        return assignmentService
                .updateStatus(
                        id,
                        request,
                        authentication);
    }

}
