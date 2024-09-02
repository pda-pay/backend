package org.ofz.management.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserCreditLimitResponse {
    private final int currentCreditLimit;
    private final int maxCreditLimit;
    private final int mortgagedTotalAmount;
}
