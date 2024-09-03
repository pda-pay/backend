package org.ofz.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MortgagedStockRequest {
    private String accountNumber;
    private String stockCode;
    private int quantity;
    private String companyCode;
}
