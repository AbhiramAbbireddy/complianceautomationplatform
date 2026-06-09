package com.abhiram.complianceautomationplatform.notification.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.abhiram.complianceautomationplatform.notification.template.EmailTemplates;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

        private final JavaMailSender mailSender;

        @Async
        public void sendHtmlEmail(
                        String to,
                        String subject,
                        String htmlContent) {

                try {

                        MimeMessage message = mailSender.createMimeMessage();

                        MimeMessageHelper helper = new MimeMessageHelper(
                                        message,
                                        true,
                                        "UTF-8");

                        helper.setTo(to);
                        helper.setSubject(subject);
                        helper.setText(
                                        htmlContent,
                                        true);

                        mailSender.send(message);

                } catch (Exception ex) {

                        log.error(
                                        "Failed to send email to {}",
                                        to,
                                        ex);
                }
        }

        public void sendAssignmentNotification(
                        String employeeEmail,
                        String employeeName,
                        String complianceTitle,
                        String assignedBy) {

                sendHtmlEmail(
                                employeeEmail,
                                "New Compliance Assigned",
                                EmailTemplates.assignmentTemplate(
                                                employeeName,
                                                complianceTitle,
                                                assignedBy));
        }

        public void sendCompletionNotification(
                        String managerEmail,
                        String managerName,
                        String complianceTitle,
                        String employeeName) {

                sendHtmlEmail(
                                managerEmail,
                                "Compliance Completed",
                                EmailTemplates.completionTemplate(
                                                managerName,
                                                complianceTitle,
                                                employeeName));
        }

        public void sendVerificationNotification(
                        String employeeEmail,
                        String employeeName,
                        String complianceTitle) {

                sendHtmlEmail(
                                employeeEmail,
                                "Compliance Verified",
                                EmailTemplates.verificationTemplate(
                                                employeeName,
                                                complianceTitle));
        }

        public void sendReminderNotification(
                        String employeeEmail,
                        String employeeName,
                        String complianceTitle,
                        long daysLeft) {

                sendHtmlEmail(
                                employeeEmail,
                                "Compliance Reminder",
                                EmailTemplates.reminderTemplate(
                                                employeeName,
                                                complianceTitle,
                                                daysLeft));
        }

        public void sendOverdueNotification(
                        String employeeEmail,
                        String employeeName,
                        String complianceTitle) {

                sendHtmlEmail(
                                employeeEmail,
                                "Compliance Overdue",
                                EmailTemplates.overdueTemplate(
                                                employeeName,
                                                complianceTitle));
        }
}