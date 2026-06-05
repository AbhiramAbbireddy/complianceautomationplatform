package com.abhiram.complianceautomationplatform.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String companyName;

    @Email
    private String companyEmail;

    @NotBlank
    private String ownerName;

    @Email
    private String ownerEmail;

    @NotBlank
    private String password;
}