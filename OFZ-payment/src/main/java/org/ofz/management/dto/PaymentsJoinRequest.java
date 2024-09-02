package org.ofz.management.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PaymentsJoinRequest {
    private final String userId;
    private final int creditLimit;
    private final int repaymentDate;
    private final String password;
    private final String repaymentAccountNumber;
    private final List<MortgagedStockRequest> mortgagedStocks;
    private final List<StockPriorityRequest> priorityStocks;
}
