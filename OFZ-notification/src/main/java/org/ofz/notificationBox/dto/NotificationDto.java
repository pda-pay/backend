package org.ofz.notificationBox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.ofz.rabbitMQ.NotificationType;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private NotificationType category;
}
