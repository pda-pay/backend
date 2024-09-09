package org.ofz.admin.service;

import lombok.RequiredArgsConstructor;
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

    private final static String SIMPLE_PAYMENT_ID = "simple";
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

        if (simplePaymentEmitter != null) {
            try {

                simplePaymentEmitter.send(SseEmitter.event().
                        name(SIMPLE_PAYMENT_ID).
                        data(log));
            } catch (IOException e) {

                removeEmitter();
                throw new SimplePaymentSseException("간편 결제 SSE 에러: " + e.getMessage());
            }
        }
    }

    @RabbitListener(queues = SIMPLE_PAYMENT_ID)
    public void receiveMessage(SimplePaymentLogDTO log) {

        try {
            Thread.sleep(1000);
            sendLogEvent(log);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new SimplePaymentMQException("간편 결제 메시지 큐 에러: " + e.getMessage());
        }
    }
}
