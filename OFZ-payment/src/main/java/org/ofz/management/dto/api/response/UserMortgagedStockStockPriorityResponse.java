package org.ofz.management.dto.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.ofz.management.dto.common.MortgagedStockDto;
import org.ofz.management.dto.common.StockPriorityDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserMortgagedStockStockPriorityResponse {
    private List<MortgagedStockDto> mortgagedStocks;
    private List<StockPriorityDto> stockPriorities;

    public void addMortgagedStock(MortgagedStockDto mortgagedStockDto) {
        mortgagedStocks.add(mortgagedStockDto);
    }

    public void addStockPriority(StockPriorityDto stockPriorityDto) {
        stockPriorities.add(stockPriorityDto);
    }
}
