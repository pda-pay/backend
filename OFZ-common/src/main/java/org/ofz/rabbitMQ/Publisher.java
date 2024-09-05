package org.ofz.rabbitMQ;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Publisher<T extends Queueable> {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitAdmin rabbitAdmin;
    private void configureQueue(String queueName) {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", Duration.ofHours(24).toMillis());
        args.put("x-max-length", 10000);
        args.put("x-max-length-bytes", 1048576);
        Queue queue = new Queue(queueName, true, false, false, args);
        rabbitAdmin.declareQueue(queue);
    }

    public void sendMessage(T queueable) {
        configureQueue(queueable.getQeueueName());
        rabbitTemplate.convertAndSend(queueable.getQeueueName(), queueable);
    }
}
