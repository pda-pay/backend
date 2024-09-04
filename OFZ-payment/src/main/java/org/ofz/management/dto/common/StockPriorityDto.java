package org.ofz.management.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPriorityDto {
    private String accountNumber;
    private int quantity;
    private String stockCode;
    private String stockName;
    private int stockRank;
    private String companyCode;
    private String companyName;
    private int stabilityLevel;
    private int stockPrice;
    private double limitPrice;
}
