package org.ofz.rabbitMQ.rabbitDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ofz.rabbitMQ.Queueable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentScheduleSuccessLogDTO implements Queueable {
    private Long userId;
    private String loginId;
    private String status; // "SUCCESS"
    private int previousMonthDebt;

    @Override
    public String getQueueName() {
        return null;
    }
}
