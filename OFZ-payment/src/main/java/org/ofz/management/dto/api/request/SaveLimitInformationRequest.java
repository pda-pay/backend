package org.ofz.management.dto.api.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SaveLimitInformationRequest {
    private final String loginId;
    private final int currentLimit;
}
