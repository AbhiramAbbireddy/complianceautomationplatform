package com.abhiram.complianceautomationplatform.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhiram.complianceautomationplatform.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
}
