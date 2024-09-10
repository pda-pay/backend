package org.ofz.admin.service;

import lombok.RequiredArgsConstructor;
import org.ofz.rabbitMQ.rabbitDto.RepaymentScheduleFailureLogDTO;
import org.ofz.rabbitMQ.rabbitDto.RepaymentSchedulePartialLogDTO;
import org.ofz.rabbitMQ.rabbitDto.RepaymentScheduleSuccessLogDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RepaymentScheduleSseService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final String SUCCESS_QUEUE = "repaymentSuccessQueue";
    private static final String PARTIAL_QUEUE = "repaymentPartialQueue";
    private static final String FAILURE_QUEUE = "repaymentFailureQueue";

    // SSE 연결 추가
    public SseEmitter addEmitter(Long userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitters.put(userId, emitter);
        return emitter;
    }

    // SSE 연결 제거
    public void removeEmitter(Long userId) {
        emitters.remove(userId);
    }

    // 성공 알림 전송
    public void sendSuccessEvent(RepaymentScheduleSuccessLogDTO successDTO) {
        sendEvent(successDTO.getUserId(), "repayment-success", successDTO);
    }

    // 일부 상환 알림 전송
    public void sendPartialEvent(RepaymentSchedulePartialLogDTO partialDTO) {
        sendEvent(partialDTO.getUserId(), "repayment-partial", partialDTO);
    }

    // 상환 실패 알림 전송
    public void sendFailureEvent(RepaymentScheduleFailureLogDTO failureDTO) {
        sendEvent(failureDTO.getUserId(), "repayment-failure", failureDTO);
    }

    // 공통 메서드로 이벤트 전송
    private void sendEvent(Long userId, String eventName, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
            } catch (IOException e) {
                removeEmitter(userId);
                throw new RuntimeException("SSE 전송 에러: " + e.getMessage());
            }
        }
    }

    // RepaymentScheduleSuccessLogDTO에 대한 RabbitListener
    @RabbitListener(queues = SUCCESS_QUEUE)
    public void receiveSuccessMessage(RepaymentScheduleSuccessLogDTO log) {
        try {
            Thread.sleep(1000);
            sendSuccessEvent(log);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("RepaymentScheduleSuccessLog MQ 에러: " + e.getMessage());
        }
    }

    // RepaymentSchedulePartialLogDTO에 대한 RabbitListener
    @RabbitListener(queues = PARTIAL_QUEUE)
    public void receivePartialMessage(RepaymentSchedulePartialLogDTO log) {
        try {
            Thread.sleep(1000);
            sendPartialEvent(log);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("RepaymentSchedulePartialLog MQ 에러: " + e.getMessage());
        }
    }

    // RepaymentScheduleFailureLogDTO에 대한 RabbitListener
    @RabbitListener(queues = FAILURE_QUEUE)
    public void receiveFailureMessage(RepaymentScheduleFailureLogDTO log) {
        try {
            Thread.sleep(1000);
            sendFailureEvent(log);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("RepaymentScheduleFailureLog MQ 에러: " + e.getMessage());
        }
    }
}
