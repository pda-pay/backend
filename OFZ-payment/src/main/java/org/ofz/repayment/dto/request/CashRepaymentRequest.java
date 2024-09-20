package org.ofz.repayment.dto.request;

import lombok.Getter;

@Getter
public class CashRepaymentRequest {

    private Long userId;
    private int amount;

    public CashRepaymentRequest() {}

    public CashRepaymentRequest(Long userId, int amount) {
        this.amount = amount;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
