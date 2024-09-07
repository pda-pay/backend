package org.ofz.repayment.dto.request;

import lombok.Getter;

@Getter
public class CashPrepaymentRequest {

    private Long userId;
    private int amount;

    public CashPrepaymentRequest() {}

    public CashPrepaymentRequest(Long userId, int amount) {
        this.amount = amount;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
