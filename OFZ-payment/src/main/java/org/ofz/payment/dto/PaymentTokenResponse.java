package org.ofz.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentTokenResponse {

    private String token;

    public PaymentTokenResponse() {}

    public PaymentTokenResponse(String token) {
        this.token = token;
    }
}
