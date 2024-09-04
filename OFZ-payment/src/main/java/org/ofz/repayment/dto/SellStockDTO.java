package org.ofz.repayment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellStockDTO {

    private int quantity;
    private String accountNumber;
    private String stockCode;
}
