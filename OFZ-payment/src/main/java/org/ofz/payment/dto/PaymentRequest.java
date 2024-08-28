package org.ofz.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentRequest {

    private String transactionId;
    private int franchiseCode;
    private int paymentAmount;
    private Long userId;

    public PaymentRequest() {}

    public PaymentRequest(String transactionId, int franchiseCode, int paymentAmount, Long userId) {
        this.transactionId = transactionId;
        this.franchiseCode = franchiseCode;
        this.paymentAmount = paymentAmount;
        this.userId = userId; //
    }
}
