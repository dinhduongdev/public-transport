package com.publictransport.repositories;

import com.publictransport.dto.NotificationDTO;
import com.publictransport.models.Notification;

import java.util.List;

public interface NotificationRepository {
    Notification save(Notification notification);
    List<NotificationDTO> findByUserId(Long userId);
    Notification findById(Long id); // Trả về entity
    NotificationDTO findDTOById(Long id); // Trả về DTO
}
