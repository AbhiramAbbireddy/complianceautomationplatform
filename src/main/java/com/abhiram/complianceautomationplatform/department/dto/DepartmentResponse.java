package com.abhiram.complianceautomationplatform.department.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DepartmentResponse {
    private Long id;

    private String name;
}