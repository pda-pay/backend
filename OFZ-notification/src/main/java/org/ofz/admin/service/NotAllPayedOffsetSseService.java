package org.ofz.admin.service;

import lombok.RequiredArgsConstructor;
import org.ofz.rabbitMQ.rabbitDto.NotAllPayedOffsetLogDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class NotAllPayedOffsetSseService implements SseService<NotAllPayedOffsetLogDto>{
    private final static String NOT_ALL_PAYED_OFFSET_ID = "adminOffsetNotAllPaid";
    private SseEmitter notAllPayedOffsetEmitter;

    @Override
    public void addEmitter(SseEmitter emitter) {
        this.notAllPayedOffsetEmitter = emitter;
    }

    @Override
    public void removeEmitter() {
        this.notAllPayedOffsetEmitter = null;
    }

    @Override
    public void sendLogEvent(NotAllPayedOffsetLogDto log) {
        if (notAllPayedOffsetEmitter != null) {
            try {
                notAllPayedOffsetEmitter.send(SseEmitter.event()
                        .name(NOT_ALL_PAYED_OFFSET_ID)
                        .data(log));
            } catch (IOException e) {
                removeEmitter();
                throw new RuntimeException("NotAllPayedOffsetSSE 에러: " + e.getMessage());
            }
        }
    }

    @RabbitListener(queues = NOT_ALL_PAYED_OFFSET_ID)
    public void receiveMessage(NotAllPayedOffsetLogDto log) {
        try{
            Thread.sleep(1000);
            sendLogEvent(log);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("NotAllPayedOffset MQ 에러: " + e.getMessage());
        }
    }
}
