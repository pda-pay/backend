package org.ofz.management.dto.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.ofz.management.dto.common.AccountDto;
import org.ofz.management.dto.common.MortgagedStockDto;
import org.ofz.management.dto.common.StockPriorityDto;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PaymentInformationResponse {
    private AccountDto repaymentAccount;
    private int repaymentDate;
    private int currentLimit;
    private List<MortgagedStockDto> mortgagedStocks;
    private List<StockPriorityDto> stockPriorities;

    public void addMortgagedStock(MortgagedStockDto mortgagedStockDto) {
        mortgagedStocks.add(mortgagedStockDto);
    }

    public void addStockPriority(StockPriorityDto stockPriorityDto) {
        stockPriorities.add(stockPriorityDto);
    }
}
