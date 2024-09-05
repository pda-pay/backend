package org.ofz.management.dto.api.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserLimitResponse {
    private final int totalLimit;
    private final int currentLimit;
}
