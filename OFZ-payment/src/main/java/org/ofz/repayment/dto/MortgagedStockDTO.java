package org.ofz.repayment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.ofz.management.entity.MortgagedStock;
import org.ofz.management.entity.StockPriority;
import org.ofz.management.entity.StockInformation;
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

    public static MortgagedStockDTO fromMortgagedStock(MortgagedStock mortgagedStock, StockInformation stockInformation, int previousPrice, int presentValue) {
        return new MortgagedStockDTO(
                0,
                mortgagedStock.getAccountNumber(),
                mortgagedStock.getQuantity(),
                mortgagedStock.getStockCode(),
                stockInformation.getName(),
                mortgagedStock.getCompanyCode(),
                String.valueOf(SecuritiesCategory.getCompanyNamefromCode(mortgagedStock.getCompanyCode())),
                stockInformation.getStabilityLevel(),
                presentValue,
                calculateLimitPrice(stockInformation.getStabilityLevel(), previousPrice),
                StockStability.getPercentByGroup(stockInformation.getStabilityLevel())
        );
    }

    public static MortgagedStockDTO fromStockPriority(StockPriority stockPriority, StockInformation stockInformation, int previousPrice, int presentValue) {
        return new MortgagedStockDTO(
                stockPriority.getStockRank(),
                stockPriority.getAccountNumber(),
                stockPriority.getQuantity(),
                stockPriority.getStockCode(),
                stockInformation.getName(),
                stockPriority.getCompanyCode(),
                String.valueOf(SecuritiesCategory.getCompanyNamefromCode(stockPriority.getCompanyCode())),
                stockInformation.getStabilityLevel(),
                presentValue,
                calculateLimitPrice(stockInformation.getStabilityLevel(), previousPrice),
                StockStability.getPercentByGroup(stockInformation.getStabilityLevel())
        );
    }

    // 가격 계산 로직
    private static double calculateLimitPrice(int stabilityLevel, int previousPrice) {
        double limitRate = StockStability.getPercentByGroup(stabilityLevel);
        double price = previousPrice * limitRate;
        return Math.round(price * 100.0) / 100.0;
    }
}
