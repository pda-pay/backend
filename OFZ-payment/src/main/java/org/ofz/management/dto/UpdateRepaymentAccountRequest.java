package org.ofz.management.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateRepaymentAccountRequest {
    private final String userId;
    private final String repaymentAccountNumber;
}
