package org.ofz.management.dto.api.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JoinPaymentServiceRequest {
    private String loginId;
    private String paymentPassword;

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
}
