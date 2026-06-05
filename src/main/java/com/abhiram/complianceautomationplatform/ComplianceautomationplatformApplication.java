package com.abhiram.complianceautomationplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.abhiram.complianceautomationplatform.security.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class ComplianceautomationplatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComplianceautomationplatformApplication.class, args);
	}

}
