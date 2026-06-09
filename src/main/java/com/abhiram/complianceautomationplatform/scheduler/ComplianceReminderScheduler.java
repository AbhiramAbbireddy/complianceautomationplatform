package com.abhiram.complianceautomationplatform.scheduler;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.abhiram.complianceautomationplatform.assignment.entity.ComplianceAssignment;
import com.abhiram.complianceautomationplatform.assignment.repository.ComplianceAssignmentRepository;
import com.abhiram.complianceautomationplatform.common.enums.ComplianceStatus;
import com.abhiram.complianceautomationplatform.notification.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ComplianceReminderScheduler {

    private final ComplianceAssignmentRepository assignmentRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void sendComplianceReminders() {

        List<ComplianceAssignment> assignments = assignmentRepository.findByCompliance_StatusNot(
                ComplianceStatus.VERIFIED);

        LocalDate today = LocalDate.now();

        for (ComplianceAssignment assignment : assignments) {

            var employee = assignment.getAssignedTo();
            var compliance = assignment.getCompliance();

            LocalDate dueDate = compliance.getDueDate();

            long daysLeft = ChronoUnit.DAYS.between(
                    today,
                    dueDate);

            if (daysLeft == 3 &&
                    !assignment.isReminder3DaySent()) {

                emailService.sendReminderNotification(
                        employee.getEmail(),
                        employee.getName(),
                        compliance.getTitle(),
                        3);

                assignment.setReminder3DaySent(true);

                log.info(
                        "3-Day Reminder Sent To {}",
                        employee.getEmail());
            }

            if (daysLeft == 1 &&
                    !assignment.isReminder1DaySent()) {

                emailService.sendReminderNotification(
                        employee.getEmail(),
                        employee.getName(),
                        compliance.getTitle(),
                        1);

                assignment.setReminder1DaySent(true);

                log.info(
                        "1-Day Reminder Sent To {}",
                        employee.getEmail());
            }

            if (daysLeft < 0 &&
                    !assignment.isOverdueReminderSent()) {

                emailService.sendOverdueNotification(
                        employee.getEmail(),
                        employee.getName(),
                        compliance.getTitle());

                assignment.setOverdueReminderSent(true);

                log.info(
                        "Overdue Reminder Sent To {}",
                        employee.getEmail());
            }

            assignmentRepository.save(assignment);

            log.info(
                    "{} -> {} days left",
                    employee.getName(),
                    daysLeft);
        }
    }
}