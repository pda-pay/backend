package org.ofz.repayment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentAccountResponse {

    private int totalDebt;
    private String accountNumber;
    private String companyCode;
}
