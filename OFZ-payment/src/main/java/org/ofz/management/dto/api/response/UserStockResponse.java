package org.ofz.management.dto.api.response;


import lombok.*;
import org.ofz.management.dto.common.StockMortgagedStockDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserStockResponse {
    private List<StockMortgagedStockDto> stockMortgagedStockDtos;
    private int totalDebt;

    public void addStockMortgagedStock(StockMortgagedStockDto stockMortgagedStockDto) {
        stockMortgagedStockDtos.add(stockMortgagedStockDto);
    }
}
