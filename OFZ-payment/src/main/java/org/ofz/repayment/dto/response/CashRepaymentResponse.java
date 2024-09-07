package org.ofz.repayment.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
public class CashRepaymentResponse {

    private int repaymentAmount;
    private String message;

    public CashRepaymentResponse() {}

    public CashRepaymentResponse(int repaymentAmount, String message) {
        this.repaymentAmount = repaymentAmount;
        this.message = message;
    }
}
