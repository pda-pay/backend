package org.ofz.rabbitMQ.rabbitDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ofz.rabbitMQ.Queueable;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentSchedulePartialLogDTO implements Queueable {
    private Long userId;
    private String loginId;
    private String status; // "PARTIAL"
    private int accountDeposit; // 일부 상환 금액
    private int remainingDebt; // 잔여 연체 대금
    private LocalDate overdueDay; // 연체일

    @Override
    public String getQueueName() {
        return null;
    }
}
