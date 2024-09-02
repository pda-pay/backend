package org.ofz.management.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateRepaymentAccountResponse {
    private final String userId;
    private final String message;

    public static UpdateRepaymentAccountResponse success(String userId) {
        return new UpdateRepaymentAccountResponse(userId, "RepaymentAccount Change successfully.");
    }
}
