package org.ofz.management.dto.api.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JoinPaymentServiceRequest {
    private final String loginId;
    private final String paymentPassword;
}
