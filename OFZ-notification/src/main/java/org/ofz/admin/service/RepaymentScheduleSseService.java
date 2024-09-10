package org.ofz.admin.service;

import lombok.RequiredArgsConstructor;
import org.ofz.rabbitMQ.rabbitDto.RepaymentScheduleFailureLogDTO;
import org.ofz.rabbitMQ.rabbitDto.RepaymentSchedulePartialLogDTO;
import org.ofz.rabbitMQ.rabbitDto.RepaymentScheduleSuccessLogDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RepaymentScheduleSseService {

    private SseEmitter commonEmitter;

    // SSE 연결 추가
    public SseEmitter addCommonEmitter() {
        commonEmitter = new SseEmitter(Long.MAX_VALUE);
        commonEmitter.onCompletion(this::removeCommonEmitter);
        commonEmitter.onTimeout(this::removeCommonEmitter);
        return commonEmitter;
    }

    // SSE 연결 제거
    public void removeCommonEmitter() {
        commonEmitter = null;
    }

    // 성공 알림 전송
    public void sendSuccessEvent(RepaymentScheduleSuccessLogDTO successDTO) {
        sendEvent("repayment-success", successDTO);
    }

    // 일부 상환 알림 전송
    public void sendPartialEvent(RepaymentSchedulePartialLogDTO partialDTO) {
        sendEvent("repayment-partial", partialDTO);
    }

    // 상환 실패 알림 전송
    public void sendFailureEvent(RepaymentScheduleFailureLogDTO failureDTO) {
        sendEvent("repayment-failure", failureDTO);
    }

    // 공통 메서드로 이벤트 전송
    private void sendEvent(String eventName, Object data) {
        if (commonEmitter != null) {
            try {
                commonEmitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
            } catch (IOException e) {
                removeCommonEmitter();
                throw new RuntimeException("SSE 전송 에러: " + e.getMessage());
            }
        }
    }
}
