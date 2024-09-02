package org.ofz.management.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateCreditLimitResponse {
    private final String userId;
    private final String message;

    public static UpdateCreditLimitResponse success(String userId) {
        return new UpdateCreditLimitResponse(userId, "CreditLimit Change successfully.");
    }
}
