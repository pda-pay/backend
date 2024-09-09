package org.ofz.admin.service;

import lombok.RequiredArgsConstructor;
import org.ofz.rabbitMQ.rabbitDto.AllPayedOffsetLogDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AllPayedOffsetSseService implements SseService<AllPayedOffsetLogDto>{
    private final static String ALL_PAYED_OFFSET_ID = "adminOffsetAllPaid";
    private SseEmitter allPayedOffsetEmitter;

    @Override
    public void addEmitter(SseEmitter emitter) {
        this.allPayedOffsetEmitter = emitter;
    }

    @Override
    public void removeEmitter() {
        this.allPayedOffsetEmitter = null;
    }

    @Override
    public void sendLogEvent(AllPayedOffsetLogDto log) {
        if(allPayedOffsetEmitter != null) {
            try{
                allPayedOffsetEmitter.send(SseEmitter.event()
                        .name(ALL_PAYED_OFFSET_ID)
                        .data(log));
            } catch (IOException e){
                removeEmitter();
                throw new RuntimeException("AllPayedOffsetSSE 에러: " + e.getMessage());
            }
        }
    }

    @RabbitListener(queues = ALL_PAYED_OFFSET_ID)
    public void receiveMessage(AllPayedOffsetLogDto log){
        try{
            Thread.sleep(1000);
            sendLogEvent(log);
        } catch (Exception e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("AllPayedOffset MQ 에러: " + e.getMessage());
        }
    }
}
