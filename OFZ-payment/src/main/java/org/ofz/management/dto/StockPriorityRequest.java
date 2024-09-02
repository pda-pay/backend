package org.ofz.management.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StockPriorityRequest {
    private final String accountNumber;
    private final int stockRank;
    private final String stockCode;
    private final int quantity;
    private final String companyCode;
}
