package org.ofz.notificationBox.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ofz.notificationBox.NotificationType;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id")
    private String loginId;

    private String title;

    private String content;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    private NotificationType category;

    @Builder
    public Notification(String loginId, String title, String content, NotificationType notificationType) {
        this.loginId = loginId;
        this.title = title;
        this.content = content;
        this.category = notificationType;
        this.createdAt = LocalDate.now();
    }
}
