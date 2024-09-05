package org.ofz.management.dto.api.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SaveLimitInformationRequest {
    private String loginId;
    private int currentLimit;

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
}
