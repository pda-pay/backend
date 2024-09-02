package org.ofz.management.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class UpdateMortgagedStockResquest {
    private final String userId;
    private final List<MortgagedStockRequest> mortgagedStocks;
    private final List<StockPriorityRequest> priorityStocks;

}
