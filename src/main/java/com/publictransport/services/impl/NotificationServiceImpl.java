package com.publictransport.services.impl;

import com.publictransport.dto.NotificationDTO;
import com.publictransport.models.Notification;
import com.publictransport.models.User;
import com.publictransport.repositories.NotificationRepository;
import com.publictransport.repositories.UserRepository;
import com.publictransport.services.EmailService;
import com.publictransport.services.NotificationService;
import com.publictransport.utils.JwtUtils;
import com.publictransport.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private EmailService emailService;


    @Override
    public void createNotification(Long userId, String title, String message) {
        Notification notification = new Notification();
        User user = userRepository.findById(userId);
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);

        // gưi mail
        emailService.sendVerificationEmail(user.getEmail(), title, message + " " + LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationDTO> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    @Override
    public void markNotificationAsRead(Long notificationId) {
        NotificationDTO notificationDTO = notificationRepository.findDTOById(notificationId);
        if (notificationDTO == null) {
            throw new RuntimeException("Notification not found");
        }
        // Kiểm tra quyền sở hữu
        User user = userRepository.findById(notificationDTO.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        String currentEmail = SecurityUtils.getCurrentUserEmail();
        if (!user.getEmail().equals(currentEmail)) {
            throw new AccessDeniedException("You are not the owner of this notification");
        }

        // Cập nhật isRead
        Notification notification = notificationRepository.findById(notificationId);
        if (notification == null) {
            throw new RuntimeException("Notification not found");
        }
        notification.setIsRead(true);
        notificationRepository.save(notification);


    }

    @Override
    public NotificationDTO getNotificationById(Long notificationId) {
        NotificationDTO notificationDTO = notificationRepository.findDTOById(notificationId);
        if (notificationDTO == null) {
            throw new RuntimeException("Notification not found");
        }
        return notificationDTO;
    }

}
