package org.ofz.repayment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.ofz.management.StockPriority;
import org.ofz.management.StockInformation;
import org.ofz.management.utils.SecuritiesCategory;
import org.ofz.management.utils.StockStability;

@Getter
@AllArgsConstructor
public class MortgagedStockDTO {

    private int stockRank;
    private String accountNumber;
    private int quantity;
    private String stockCode;
    private String stockName;
    private String companyCode;
    private String companyName;
    private int stabilityLevel;
    private int presentValue;
    private double limitPrice;
    private double percent;

    public MortgagedStockDTO() {
    }

    @Builder
    public MortgagedStockDTO(StockPriority stockPriority, StockInformation stockInformation, int previousPrice, int presentValue) {
        this.stockRank = stockPriority.getStockRank();
        this.accountNumber = stockPriority.getAccountNumber();
        this.quantity = stockPriority.getQuantity();
        this.stockCode = stockPriority.getStockCode();
        this.stockName = stockInformation.getName();
        this.companyCode = stockPriority.getCompanyCode();
        this.companyName = String.valueOf(SecuritiesCategory.getCompanyNamefromCode(companyCode));
        this.stabilityLevel = stockInformation.getStabilityLevel();
        this.presentValue = presentValue;
        this.limitPrice = calculateLimitPrice(previousPrice);
        this.percent = StockStability.getPercentByGroup(stabilityLevel);
    }

    private double calculateLimitPrice(int previousPrice) {
        double limitRate = StockStability.getPercentByGroup(stabilityLevel);
        double price = previousPrice * limitRate;
        return Math.round(price * 100.0) / 100.0;
    }
}
