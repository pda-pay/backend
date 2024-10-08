package org.ofz.admin.service;

import lombok.RequiredArgsConstructor;
import org.ofz.admin.exception.mq.RepaymentMQException;
import org.ofz.admin.exception.sse.RepaymentSseException;
import org.ofz.rabbitMQ.rabbitDto.RepaymentHistoryLogDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RepaymentSseService implements SseService<RepaymentHistoryLogDTO> {

    private final static String QUEUE_NAME = "repayment";
    private SseEmitter prepaymentEmitter;

    @Override
    public void addEmitter(SseEmitter emitter) {
        this.prepaymentEmitter = emitter;
    }

    @Override
    public void removeEmitter() {
        this.prepaymentEmitter = null;
    }

    @Override
    public void sendLogEvent(RepaymentHistoryLogDTO log) {

        if (prepaymentEmitter == null) {
            System.out.println("선상환 SSE 에러: 생성된 SSE 객체가 없습니다.");
            return;
        }

        try {

            prepaymentEmitter.send(SseEmitter.event().
                    name(QUEUE_NAME).
                    data(log));
        } catch (IOException e) {

            removeEmitter();
            throw new RepaymentSseException("선상환 SSE 에러: " + e.getMessage());
        }
    }

    @RabbitListener(queues = QUEUE_NAME)
    public void receiveMessage(RepaymentHistoryLogDTO log) {

        try {
            Thread.sleep(1000);
            sendLogEvent(log);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.out.println("선상환 메시지 큐 에러: " + e.getMessage());
        }
    }
}
