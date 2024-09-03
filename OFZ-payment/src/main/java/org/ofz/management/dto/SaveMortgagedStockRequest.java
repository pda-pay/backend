package org.ofz.management.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class SaveMortgagedStockRequest {
    private final String loginId;
    private final List<MortgagedStockRequest> mortgagedStocks;
}
