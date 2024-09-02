package org.ofz.repayment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {

    private int deposit;
    private String accountNumber;
    private String companyCode;
}
