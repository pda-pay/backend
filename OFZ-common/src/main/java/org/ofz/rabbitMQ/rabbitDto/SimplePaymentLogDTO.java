package org.ofz.rabbitMQ.rabbitDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ofz.rabbitMQ.Queueable;

import java.time.LocalDateTime;

import static org.ofz.rabbitMQ.utils.DateUtil.formatLocalDateTime;

@Getter
@NoArgsConstructor
public class SimplePaymentLogDTO implements Queueable {

    private Long id;
    private String loginId;
    private int amount;
    private int franchiseCode;
    private String isSuccess;
    private String date;

    @Builder
    public SimplePaymentLogDTO(Long id, String loginId, int amount, int franchiseCode, String isSuccess, LocalDateTime date) {
        this.id = id;
        this.loginId = loginId;
        this.amount = amount;
        this.franchiseCode = franchiseCode;
        this.isSuccess = isSuccess;
        this.date = formatLocalDateTime(date);
    }

    @Override
    public String getQueueName() {
        return "simple";
    }
}
