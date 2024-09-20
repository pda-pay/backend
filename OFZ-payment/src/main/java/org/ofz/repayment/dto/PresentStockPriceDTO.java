package org.ofz.repayment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PresentStockPriceDTO {

    private String stockCode;
    private int amount;

    public PresentStockPriceDTO() {}

    public PresentStockPriceDTO(String stockCode, int amount) {
        this.stockCode = stockCode;
        this.amount = amount;
    }
}