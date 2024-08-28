package org.ofz.payment.dto;

import lombok.Getter;

@Getter
public class PaymentAuthRequest {

    private Long userId;
    private String password;

    public PaymentAuthRequest() {}

    public PaymentAuthRequest(Long userId, String password) {
        this.userId = userId;
        this.password = password;
    }
}
