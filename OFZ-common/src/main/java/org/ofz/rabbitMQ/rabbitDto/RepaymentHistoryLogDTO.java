package org.ofz.rabbitMQ.rabbitDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ofz.rabbitMQ.Queueable;

import java.time.LocalDateTime;

import static org.ofz.rabbitMQ.utils.DateUtil.formatLocalDateTime;

@Getter
@NoArgsConstructor
public class RepaymentHistoryLogDTO implements Queueable {

    private Long id;
    private String loginId;
    private int amount;
    private String accountNumber;
    private String type;
    private String date;

    @Builder
    public RepaymentHistoryLogDTO(Long id, String loginId, int amount, String accountNumber, String type, LocalDateTime date) {
        this.id = id;
        this.loginId = loginId;
        this.amount = amount;
        this.accountNumber = accountNumber;
        this.type = type;
        this.date = formatLocalDateTime(date);
    }

    @Override
    public String getQueueName() {
        return "repayment";
    }
}
