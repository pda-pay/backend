package org.ofz.repayment.dto;

import lombok.Getter;

@Getter
public class CashPrepaymentRequest {

    private Long userId;
    private int amount;

    public CashPrepaymentRequest() {}

    public CashPrepaymentRequest(Long userId, int amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
