package com.abhiram.complianceautomationplatform.auth.service;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.abhiram.complianceautomationplatform.auth.dto.LoginRequest;
import com.abhiram.complianceautomationplatform.auth.dto.LoginResponse;
import com.abhiram.complianceautomationplatform.auth.dto.RegisterRequest;
import com.abhiram.complianceautomationplatform.company.entity.Company;
import com.abhiram.complianceautomationplatform.company.repository.CompanyRepository;
import com.abhiram.complianceautomationplatform.role.RoleConstants;
import com.abhiram.complianceautomationplatform.role.entity.Role;
import com.abhiram.complianceautomationplatform.role.repository.RoleRepository;
import com.abhiram.complianceautomationplatform.security.JwtService;
import com.abhiram.complianceautomationplatform.user.entity.User;
import com.abhiram.complianceautomationplatform.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getOwnerEmail())) {
            throw new RuntimeException("User already exists");
        }

        Company company = Company.builder()
                .name(request.getCompanyName())
                .email(request.getCompanyEmail())
                .createdAt(LocalDateTime.now())
                .build();

        companyRepository.save(company);

        Role role = roleRepository.findByName(RoleConstants.OWNER)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        User owner = User.builder()
                .name(request.getOwnerName())
                .email(request.getOwnerEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .company(company)
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(owner);

        return "Company Registered Successfully";
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(
                new com.abhiram.complianceautomationplatform.security.CustomUserPrincipal(user));

        return new LoginResponse(token);
    }
}