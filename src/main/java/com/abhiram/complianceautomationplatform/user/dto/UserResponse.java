package com.abhiram.complianceautomationplatform.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;

    private String name;

    private String email;

    private String role;

    private String department;

    private String manager;
}