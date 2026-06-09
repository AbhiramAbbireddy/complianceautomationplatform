package com.abhiram.complianceautomationplatform.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.abhiram.complianceautomationplatform.security.repository.RevokedTokenRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final RevokedTokenRepository revokedTokenRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(
                    request,
                    response);

            return;
        }

        try {

            String token = authHeader.substring(7);

            String username = jwtService.extractUsername(token);

            if (username != null &&
                    SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                UserDetails userDetails = customUserDetailsService
                        .loadUserByUsername(username);

                if (!userDetails.isEnabled()) {

                    log.warn(
                            "Disabled user attempted access: {}",
                            username);

                    response.setStatus(
                            HttpServletResponse.SC_UNAUTHORIZED);

                    return;
                }

                if (jwtService.isTokenValid(
                        token,
                        userDetails)) {

                    String jti = jwtService.extractJti(token);

                    if (revokedTokenRepository
                            .existsByTokenJti(jti)) {

                        log.warn(
                                "Revoked token used by: {}",
                                username);

                        response.setStatus(
                                HttpServletResponse.SC_UNAUTHORIZED);

                        return;
                    }

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request));

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authenticationToken);
                }
            }

            filterChain.doFilter(
                    request,
                    response);

        } catch (Exception ex) {

            log.warn(
                    "Invalid JWT received: {}",
                    ex.getMessage());

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}