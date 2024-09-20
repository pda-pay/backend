package org.ofz.rabbitMQ.rabbitDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ofz.rabbitMQ.NotificationPage;
import org.ofz.rabbitMQ.NotificationType;
import org.ofz.rabbitMQ.Queueable;

@Getter
@NoArgsConstructor
@Builder
public class NotificationMessage implements Queueable {
    private String loginId;
    private String title;
    private String body;
    private NotificationType category;
    private NotificationPage page;

    @Builder
    public NotificationMessage(String loginId, String title, String body, NotificationType category, NotificationPage page) {
        this.loginId = loginId;
        this.title = title;
        this.body = body;
        this.category = category;
        this.page = page;
    }

    @Override
    public String getQueueName() {
        return "notification";
    }
}
