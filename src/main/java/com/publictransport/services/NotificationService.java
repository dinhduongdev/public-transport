package com.publictransport.services;

import com.publictransport.dto.NotificationDTO;
import com.publictransport.models.Notification;

import java.util.List;

public interface NotificationService {
    void createNotification(Long userId, String title, String message);
    List<NotificationDTO> getNotificationsByUserId(Long userId);
    void markNotificationAsRead(Long notificationId);
    NotificationDTO getNotificationById(Long notificationId);
}
