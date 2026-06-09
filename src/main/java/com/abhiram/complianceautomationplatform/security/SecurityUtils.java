package com.abhiram.complianceautomationplatform.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {
    }

    public static String getCurrentUserEmail() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null) {
            return "SYSTEM";
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserPrincipal user) {
            return user.getUsername();
        }

        return "SYSTEM";
    }
}
