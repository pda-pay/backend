package org.ofz.payment.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentRequest {

    private String transactionId;
    private int franchiseCode;
    private int paymentAmount;
    private String token;

    public PaymentRequest() {}

    public PaymentRequest(String transactionId, int franchiseCode, int paymentAmount, String token) {
        this.transactionId = transactionId;
        this.franchiseCode = franchiseCode;
        this.paymentAmount = paymentAmount;
        this.token = token;
    }
}
