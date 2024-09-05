package org.ofz.repayment.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PaymentInfoForCashResponse {

    private int paymentAmount;
    private int creditLimit;
    private int accountDeposit;
    List<PaymentHistoriesResponse.PaymentHistoryDTO> paymentHistories;

    public PaymentInfoForCashResponse() {}

    public PaymentInfoForCashResponse(int paymentAmount, int creditLimit, int accountDeposit, List<PaymentHistoriesResponse.PaymentHistoryDTO> paymentHistories) {
        this.paymentAmount = paymentAmount;
        this.creditLimit = creditLimit;
        this.accountDeposit = accountDeposit;
        this.paymentHistories = paymentHistories;
    }
}
