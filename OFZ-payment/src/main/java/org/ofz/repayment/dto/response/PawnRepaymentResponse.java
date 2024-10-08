package org.ofz.repayment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PawnRepaymentResponse {

    private int repaymentAmount;
    private int totalSellAmount;
    private int realRepaymentAmount;
    private int amountToAccount;
    private String message;
}
