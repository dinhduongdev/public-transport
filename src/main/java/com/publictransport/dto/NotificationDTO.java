package com.publictransport.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private Boolean isRead;
    private Long userId;

    public NotificationDTO(Long id, String title, String message, LocalDateTime createdAt, Boolean isRead, Long userId) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.userId = userId;
    }
}
