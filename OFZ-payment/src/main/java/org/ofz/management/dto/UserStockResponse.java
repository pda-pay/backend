package org.ofz.management.dto;

import lombok.Getter;
import org.ofz.management.entity.StockInformation;
import org.ofz.management.utils.SecuritiesCategory;
import org.ofz.management.utils.StockStability;

@Getter
public class UserStockResponse {
    private String accountNumber;
    private int quantity;
    private int mortgagedQuantity;
    private String stockCode;
    private String stockName;
    private String companyCode;
    private String companyName;
    private int stabilityLevel;
    private int stockPrice;
    private double limitPrice;

    public UserStockResponse(UserStockProjection userStockProjection, int stockPrice, StockInformation stockInformation) {
        accountNumber = userStockProjection.getAccountNumber();
        quantity = userStockProjection.getQuantity();
        mortgagedQuantity = userStockProjection.getMortgagedQuantity();
        stockCode = userStockProjection.getStockCode();
        stockName = stockInformation.getName();
        companyCode = userStockProjection.getCompanyCode();
        companyName = String.valueOf(SecuritiesCategory.getCompanyNamefromCode(companyCode));
        this.stockPrice = stockPrice;
        this.stabilityLevel = stockInformation.getStabilityLevel();
        this.limitPrice = calculateLimitPrice();
    }

    private double calculateLimitPrice() {
        double limitRate = StockStability.getPercentByGroup(stabilityLevel);
        double price = stockPrice * limitRate;
        return Math.round(price * 100.0) / 100.0;
    }
}
