package org.ofz.payment.dto.request;

import lombok.Getter;

@Getter
public class PaymentAuthRequest {

    private Long userId;
    private String paymentPassword;

    public PaymentAuthRequest() {}

    public PaymentAuthRequest(String paymentPassword) {
        this.paymentPassword = paymentPassword;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
