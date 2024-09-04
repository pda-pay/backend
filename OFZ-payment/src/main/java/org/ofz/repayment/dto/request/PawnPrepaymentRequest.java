package org.ofz.repayment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PawnPrepaymentRequest {

    private Long userId;
    private int repaymentAmount;
    private List<SelectedStock> selectedStocks;

    @Getter
    public static class SelectedStock {
        private int stockRank;
        private int quantity;
        private String accountNumber;
        private String stockCode;

        public SelectedStock() {}

        public SelectedStock(int stockRank, int quantity, String accountNumber, String stockCode) {
            this.stockRank = stockRank;
            this.quantity = quantity;
            this.accountNumber = accountNumber;
            this.stockCode = stockCode;
        }
    }
}
