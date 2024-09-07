package org.ofz.management.dto.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class UserLimitResponse {
    private final int currentLimit;
    private final double totalLimit;
    private final int totalMortgagedPrice;
    private final double mortgagedMaintenanceRate;
    private final int totalPaymentAmount;

    @Builder
    public UserLimitResponse(int currentLimit, double totalLimit, int totalMortgagedPrice, int totalPaymentAmount) {
        this.currentLimit = currentLimit;
        this.totalLimit = totalLimit;
        this.totalMortgagedPrice = totalMortgagedPrice;
        this.mortgagedMaintenanceRate = calculateMortgageMaintenanceRate(currentLimit, totalMortgagedPrice);
        this.totalPaymentAmount = totalPaymentAmount;
    }

    private double calculateMortgageMaintenanceRate(int currentLimit, int totalMortgagedPrice) {
        if (currentLimit == 0) {
            throw new ArithmeticException("currentLimit zero");
        }

        double rate = totalMortgagedPrice / currentLimit;
        return Math.round(rate * 1000.0) / 1000.0;
    }
}
