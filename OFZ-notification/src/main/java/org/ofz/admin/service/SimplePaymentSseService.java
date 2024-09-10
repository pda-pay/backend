package org.ofz.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ofz.admin.exception.mq.SimplePaymentMQException;
import org.ofz.admin.exception.sse.SimplePaymentSseException;
import org.ofz.rabbitMQ.rabbitDto.SimplePaymentLogDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SimplePaymentSseService implements SseService<SimplePaymentLogDTO> {

    private final static String QUEUE_NAME = "simple";
    private SseEmitter simplePaymentEmitter;

    @Override
    public void addEmitter(SseEmitter emitter) {
        this.simplePaymentEmitter = emitter;
    }

    @Override
    public void removeEmitter() {
        this.simplePaymentEmitter = null;
    }

    @Override
    public void sendLogEvent(SimplePaymentLogDTO log) {

        if (simplePaymentEmitter == null) {
            System.out.println("간편 결제 SSE 에러: 생성된 SSE 객체가 없습니다.");
            return;
        }

        try {

            simplePaymentEmitter.send(SseEmitter.event().
                    name(QUEUE_NAME).
                    data(log));
        } catch (IOException e) {

            removeEmitter();
            throw new SimplePaymentSseException("간편 결제 SSE 에러: " + e.getMessage());
        }
    }

    @RabbitListener(queues = QUEUE_NAME)
    public void receiveMessage(SimplePaymentLogDTO log) {

        try {
            Thread.sleep(1000);
            sendLogEvent(log);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("간편 결제 메시지 큐 에러: " + e.getMessage());
        }
    }
}
