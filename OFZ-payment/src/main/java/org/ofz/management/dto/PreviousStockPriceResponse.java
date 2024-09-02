package org.ofz.management.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PreviousStockPriceResponse {
    private final String stockCode;
    private final int amount;
}
