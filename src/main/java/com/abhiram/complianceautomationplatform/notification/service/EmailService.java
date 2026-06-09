package com.abhiram.complianceautomationplatform.notification.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(
            String to,
            String subject,
            String body) {

        try {

            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

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
            String complianceTitle) {

        sendEmail(
                employeeEmail,
                "New Compliance Assigned",
                "Hello " + employeeName
                        + ",\n\n"
                        + "A new compliance has been assigned to you.\n\n"
                        + "Compliance: " + complianceTitle
                        + "\n\nPlease login and complete it before the due date.");
    }

    public void sendCompletionNotification(
            String managerEmail,
            String managerName,
            String complianceTitle,
            String employeeName) {

        sendEmail(
                managerEmail,
                "Compliance Completed",
                "Hello " + managerName
                        + ",\n\n"
                        + employeeName
                        + " has completed the compliance:\n\n"
                        + complianceTitle
                        + "\n\nPlease review and verify it.");
    }

    public void sendVerificationNotification(
            String employeeEmail,
            String employeeName,
            String complianceTitle) {

        sendEmail(
                employeeEmail,
                "Compliance Verified",
                "Hello " + employeeName
                        + ",\n\n"
                        + "Your compliance has been verified.\n\n"
                        + "Compliance: "
                        + complianceTitle
                        + "\n\nGreat job!");
    }
}