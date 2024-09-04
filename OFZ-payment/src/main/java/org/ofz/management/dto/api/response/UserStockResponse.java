package org.ofz.management.dto.api.response;


import lombok.*;
import org.ofz.management.dto.common.StockMortgagedStockDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserStockResponse {
    private List<StockMortgagedStockDto> stockMortgagedStocks;
    private int totalDebt;

    public void addStockMortgagedStock(StockMortgagedStockDto stockMortgagedStockDto) {
        stockMortgagedStocks.add(stockMortgagedStockDto);
    }
}
