package com.abhiram.complianceautomationplatform.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.abhiram.complianceautomationplatform.company.entity.Company;
import com.abhiram.complianceautomationplatform.department.entity.Department;
import com.abhiram.complianceautomationplatform.role.entity.Role;
import com.abhiram.complianceautomationplatform.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByEmail(String email);

        boolean existsByEmail(String email);

        List<User> findByCompany(Company company);

        Optional<User> findByIdAndCompany(
                        Long id,
                        Company company);

        List<User> findByManager(User manager);

        long countByCompany(Company company);

        long countByDepartment(
                        Department department);

        @Query("""
                        select u
                        from User u
                        left join fetch u.department
                        where u.id=:id
                        """)

        Optional<User> findByIdWithDepartment(
                        @Param("id") Long id);

        long countByDepartmentAndRole(
                        Department department,
                        Role role);

        List<User> findByDepartmentAndRole(
                        Department department,
                        Role role);
}