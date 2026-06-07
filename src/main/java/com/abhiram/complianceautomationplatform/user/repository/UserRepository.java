package com.abhiram.complianceautomationplatform.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhiram.complianceautomationplatform.company.entity.Company;
import com.abhiram.complianceautomationplatform.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByCompany(Company company);

    Optional<User> findByIdAndCompany(
            Long id,
            Company company);

    List<User> findByManager(User manager);
}