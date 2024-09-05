package org.ofz.rabbitMQ.rabbitDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.ofz.rabbitMQ.Queueable;

@Getter
@AllArgsConstructor
public class Example implements Queueable {
    private String message;
    private String example;
    @Override
    public String getQeueueName() {
        return "example";
    }
}