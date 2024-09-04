package org.ofz.repayment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.ofz.repayment.dto.MortgagedStockDTO;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class PaymentInfoForPawnResponse {

    private int totalDebt;
    private List<MortgagedStockDTO> mortgagedStocks;

    public void addMortgagedStock(MortgagedStockDTO mortgagedStock) {
        mortgagedStocks.add(mortgagedStock);
    }

    public PaymentInfoForPawnResponse(int totalDebt) {
        this.totalDebt = totalDebt;
        this.mortgagedStocks = new ArrayList<>();
    }

    public PaymentInfoForPawnResponse() {}
}
