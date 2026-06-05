package com.abhiram.complianceautomationplatform.user.entity;

import java.time.LocalDateTime;

import com.abhiram.complianceautomationplatform.company.entity.Company;
import com.abhiram.complianceautomationplatform.role.entity.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false,unique=true)
    private String email;

    @Column(nullable=false)
    private String password;

    private boolean enabled;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="company_id")
    private Company company;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="role_id")
    private Role role;

    private LocalDateTime createdAt;
}