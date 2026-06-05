package com.abhiram.complianceautomationplatform.assignment.entity;

import java.time.LocalDateTime;

import com.abhiram.complianceautomationplatform.compliance.entity.Compliance;
import com.abhiram.complianceautomationplatform.user.entity.User;

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
@Table(name="compliance_assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplianceAssignment
{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="compliance_id")
    private Compliance compliance;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="assigned_to")
    private User assignedTo;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="assigned_by")
    private User assignedBy;

    private LocalDateTime assignedAt;
}