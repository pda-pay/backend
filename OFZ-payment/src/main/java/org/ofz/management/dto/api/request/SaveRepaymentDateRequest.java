package org.ofz.management.dto.api.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ofz.management.dto.common.StockPriorityDto;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SaveRepaymentDateRequest {
    private String loginId;
    private int repaymentDate;

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
}
