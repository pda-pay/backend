package org.ofz.rabbitMQ.rabbitDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Builder
    public NotificationMessage(String loginId, String title, String body, String category) {
        this.loginId = loginId;
        this.title = title;
        this.body = body;
        this.category = category;
    }

    @Override
    public String getQueueName() {
        return "notification";
    }
}
