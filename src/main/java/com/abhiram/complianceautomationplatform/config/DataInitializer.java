package com.abhiram.complianceautomationplatform.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.abhiram.complianceautomationplatform.role.RoleConstants;
import com.abhiram.complianceautomationplatform.role.entity.Role;
import com.abhiram.complianceautomationplatform.role.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        createRoleIfNotExists(RoleConstants.OWNER);
        createRoleIfNotExists(RoleConstants.COMPLIANCE_MANAGER);
        createRoleIfNotExists(RoleConstants.DEPARTMENT_MANAGER);
        createRoleIfNotExists(RoleConstants.EMPLOYEE);
        createRoleIfNotExists(RoleConstants.AUDITOR);
    }

    private void createRoleIfNotExists(String roleName) {
        if(roleRepository.findByName(roleName).isEmpty())
        {
            Role role=new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}