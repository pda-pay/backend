package org.ofz.notificationBox;

import lombok.RequiredArgsConstructor;
import org.ofz.notificationBox.dto.NotificationDto;
import org.ofz.notificationBox.entity.Notification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationBoxService {
    private final NotificationBoxRepository notificationBoxRepository;

    @Transactional
    public List<NotificationDto> getNotifications(Long userId){
        List<Notification> notifications = notificationBoxRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream().map(notification ->
                new NotificationDto(
                        notification.getId(),
                        notification.getTitle(),
                        notification.getContent(),
                        notification.getCreatedAt(),
                        notification.getCategory()
                )).collect(Collectors.toList());
    }

    @Transactional
    public void deleteNotificationsByIds(Long userId, List<Long> ids) {
        notificationBoxRepository.deleteNotificationsByUserIdAndIds(userId, ids);
    }
}
