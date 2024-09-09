package org.ofz.rabbitMQ.rabbitDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ofz.rabbitMQ.Queueable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage implements Queueable {
    private String userId;
    private String title;
    private String body;
    private String category;

    @Override
    public String getQeueueName() {
        return "nofication";
    }
}
