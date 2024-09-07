package org.ofz.payment.dto;

import lombok.Builder;
import lombok.Getter;
import org.ofz.payment.entity.Franchise;
import org.ofz.payment.entity.PaymentHistory;

import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentHistoryDTO {

    private int paymentAmount;
    private Franchise franchise;
    private Long userId;

    public PaymentHistoryDTO() {}

    public PaymentHistoryDTO(int paymentAmount, Franchise franchise, Long userId) {
        this.paymentAmount = paymentAmount;
        this.franchise = franchise;
        this.userId = userId;
    }

    public PaymentHistory toEntity() {

        return PaymentHistory.builder()
                .paymentAmount(this.paymentAmount)
                .createdAt(LocalDateTime.now())
                .franchise(this.franchise)
                .userId(this.userId)
                .build();
    }
}
