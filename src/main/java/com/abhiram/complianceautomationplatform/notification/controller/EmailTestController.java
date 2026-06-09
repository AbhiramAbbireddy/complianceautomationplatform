package com.abhiram.complianceautomationplatform.notification.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abhiram.complianceautomationplatform.notification.service.EmailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class EmailTestController {

    private final EmailService emailService;

    @GetMapping("/email")
    public String sendEmail() {

        emailService.sendEmail(
                "abhiram.wordweaver@gmail.com",
                "Compliance Platform Test",
                "Email notifications are working successfully.");

        return "Email Sent Successfully";
    }
}