package org.ofz.rabbitMQ.rabbitDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ofz.rabbitMQ.NotificationType;
import org.ofz.rabbitMQ.Queueable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationMessage implements Queueable {
    private String loginId;
    private String title;
    private String body;
    private NotificationType category;

    @Override
    public String getQueueName() {
        return "nofication";
    }
}
