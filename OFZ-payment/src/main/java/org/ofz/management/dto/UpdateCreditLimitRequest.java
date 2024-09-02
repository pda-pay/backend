package org.ofz.management.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateCreditLimitRequest {
    private final String userId;
    private final int creditLimit;
}
