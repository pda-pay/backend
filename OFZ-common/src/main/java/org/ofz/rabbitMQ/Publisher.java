package org.ofz.rabbitMQ;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class Publisher<T extends Queueable> {

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(T queueable) {
        String queueName = queueable.getQueueName();

        rabbitTemplate.convertAndSend(queueName, queueable);
    }
}
