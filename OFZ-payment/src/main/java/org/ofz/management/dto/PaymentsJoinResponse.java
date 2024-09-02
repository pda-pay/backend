package org.ofz.management.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PaymentsJoinResponse {
    private final String userId;
    private final String message;

    public static PaymentsJoinResponse success(String userId) {
        return new PaymentsJoinResponse(userId, "Join Payment Service successfully.");
    }
}
