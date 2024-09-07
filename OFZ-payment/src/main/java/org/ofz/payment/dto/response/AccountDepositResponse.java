package org.ofz.payment.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccountDepositResponse {

    private String accountNumber;
    private int paymentAmount;

    public AccountDepositResponse() {}

    public AccountDepositResponse(String accountNumber, int paymentAmount) {
        this.accountNumber = accountNumber;
        this.paymentAmount = paymentAmount;
    }
}
