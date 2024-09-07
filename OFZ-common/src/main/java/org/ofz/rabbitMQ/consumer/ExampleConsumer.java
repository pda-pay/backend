package org.ofz.rabbitMQ.consumer;

import org.ofz.rabbitMQ.rabbitDto.Example;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ExampleConsumer {
    private final static String QUEUE_NAME = "example";

    @RabbitListener(queues = QUEUE_NAME)
    public void receiveMessage(Example example) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread was interrupted");
        }
        System.out.println(example.getMessage());
    }
}