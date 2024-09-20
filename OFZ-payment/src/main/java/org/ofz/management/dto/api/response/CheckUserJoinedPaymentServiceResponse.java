package org.ofz.management.dto.api.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CheckUserJoinedPaymentServiceResponse {
    private final String userId;
    private final String name;
    private final boolean isPaymentServiceMember;
    private final boolean isPaymentAccess;
}
