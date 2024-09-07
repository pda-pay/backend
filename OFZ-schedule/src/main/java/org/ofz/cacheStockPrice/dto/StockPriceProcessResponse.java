package org.ofz.cacheStockPrice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class StockPriceProcessResponse {
    private final String message;

    public static StockPriceProcessResponse success() {
        return new StockPriceProcessResponse("cache stockPrice success!!");
    }

    public static StockPriceProcessResponse fail() {
        return new StockPriceProcessResponse("password not correct!!");
    }
}
