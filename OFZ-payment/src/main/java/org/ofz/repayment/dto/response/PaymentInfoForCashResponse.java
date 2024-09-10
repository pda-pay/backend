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
    private int remainCreditLimit;
    List<PaymentHistoriesResponse.PaymentHistoryDTO> paymentHistories;

    public PaymentInfoForCashResponse() {}

    public PaymentInfoForCashResponse(int paymentAmount, int creditLimit, int remainCreditLimit, List<PaymentHistoriesResponse.PaymentHistoryDTO> paymentHistories) {
        this.paymentAmount = paymentAmount;
        this.creditLimit = creditLimit;
        this.remainCreditLimit = remainCreditLimit;
        this.paymentHistories = paymentHistories;
    }
}
