package org.ofz.payment.dto.request;

import lombok.Getter;

@Getter
public class PaymentAuthRequest {

    private Long userId;
    private String paymentPassword;

    public PaymentAuthRequest() {}

    public PaymentAuthRequest(Long userId, String paymentPassword) {
        this.userId = userId;
        this.paymentPassword = paymentPassword;
    }
}
