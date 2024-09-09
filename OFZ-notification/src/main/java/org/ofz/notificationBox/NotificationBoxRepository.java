package org.ofz.notificationBox;

import org.ofz.notificationBox.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationBoxRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUserId(Long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.id IN :ids")
    void deleteNotificationsByIds(List<Long> ids);
}
