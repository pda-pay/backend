package org.ofz.rabbitMQ;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class QueueMaker {

    @Bean
    public Queue configureNotificationQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", Duration.ofHours(24).toMillis());
        args.put("x-max-length", 10000);
        args.put("x-max-length-bytes", 1048576);
        return new Queue("notification", true, false, false, args);
    }

    @Bean
    public Queue configureAdminOffsetAllPaidQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", Duration.ofHours(24).toMillis());
        args.put("x-max-length", 10000);
        args.put("x-max-length-bytes", 1048576);
        return new Queue("adminOffsetAllPaid", true, false, false, args);
    }

    @Bean
    public Queue configureAdminOffsetNotAllPaidQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", Duration.ofHours(24).toMillis());
        args.put("x-max-length", 10000);
        args.put("x-max-length-bytes", 1048576);
        return new Queue("adminOffsetNotAllPaid", true, false, false, args);
    }

    @Bean
    public Queue configureSimpleQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", Duration.ofHours(24).toMillis());
        args.put("x-max-length", 10000);
        args.put("x-max-length-bytes", 1048576);
        return new Queue("simple", true, false, false, args);
    }

    @Bean
    public Queue configureRepaymentQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", Duration.ofHours(24).toMillis());
        args.put("x-max-length", 10000);
        args.put("x-max-length-bytes", 1048576);
        return new Queue("repayment", true, false, false, args);
    }
}