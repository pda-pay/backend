package org.ofz.management.dto.api.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CheckUserJoinedPaymentServiceResponse {
    private final String userId;
    private final boolean isPaymentServiceMember;
}
