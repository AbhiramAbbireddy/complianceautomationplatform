package com.abhiram.complianceautomationplatform.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhiram.complianceautomationplatform.security.entity.RevokedToken;

public interface RevokedTokenRepository
        extends JpaRepository<RevokedToken, Long> {

    boolean existsByTokenJti(String tokenJti);
}