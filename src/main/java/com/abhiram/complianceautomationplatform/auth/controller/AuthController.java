package com.abhiram.complianceautomationplatform.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abhiram.complianceautomationplatform.auth.dto.LoginRequest;
import com.abhiram.complianceautomationplatform.auth.dto.LoginResponse;
import com.abhiram.complianceautomationplatform.auth.dto.RegisterRequest;
import com.abhiram.complianceautomationplatform.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication APIs")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register Company and Owner")
    public ResponseEntity<String> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(
                authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login User")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                authService.login(request));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout User")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        authService.logout(token);

        return ResponseEntity.ok(
                "Logged out successfully");
    }
}