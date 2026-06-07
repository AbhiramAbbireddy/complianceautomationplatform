package com.abhiram.complianceautomationplatform.user.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abhiram.complianceautomationplatform.user.dto.CreateDepartmentManagerRequest;
import com.abhiram.complianceautomationplatform.user.dto.CreateEmployeeRequest;
import com.abhiram.complianceautomationplatform.user.dto.UserResponse;
import com.abhiram.complianceautomationplatform.user.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {
        private final UserService userService;

        @PostMapping("/department-managers")
        @PreAuthorize("hasRole('OWNER')")
        public UserResponse createDepartmentManager(
                        @Valid @RequestBody CreateDepartmentManagerRequest request,

                        Authentication authentication) {
                return userService
                                .createDepartmentManager(
                                                request,
                                                authentication);
        }

        @GetMapping
        public List<UserResponse> getUsers(
                        Authentication authentication) {
                return userService.getUsers(
                                authentication);
        }

        @GetMapping("/{id}")
        public UserResponse getUserById(
                        @PathVariable Long id,
                        Authentication authentication) {
                return userService.getUserById(
                                id,
                                authentication);
        }

        @PostMapping("/employees")
        @PreAuthorize("hasRole('OWNER') or hasRole('DEPARTMENT_MANAGER')")
        public UserResponse createEmployee(
                        @Valid @RequestBody CreateEmployeeRequest request,

                        Authentication authentication) {
                return userService.createEmployee(
                                request,
                                authentication);
        }

        @GetMapping("/my-team")
        @PreAuthorize("hasRole('DEPARTMENT_MANAGER')")
        public List<UserResponse> getMyTeam(
                        Authentication authentication) {
                return userService.getMyTeam(
                                authentication);
        }
}
