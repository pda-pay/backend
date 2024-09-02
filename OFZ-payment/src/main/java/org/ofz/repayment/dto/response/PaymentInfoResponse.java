package org.ofz.repayment.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PaymentInfoResponse {

    private int paymentAmount;
    private int creditLimit;
    private int accountDeposit;
    List<PaymentHistoriesResponse.PaymentHistoryDTO> paymentHistories;

    public PaymentInfoResponse() {}

    public PaymentInfoResponse(int paymentAmount, int creditLimit, int accountDeposit, List<PaymentHistoriesResponse.PaymentHistoryDTO> paymentHistories) {
        this.paymentAmount = paymentAmount;
        this.creditLimit = creditLimit;
        this.accountDeposit = accountDeposit;
        this.paymentHistories = paymentHistories;
    }
}
