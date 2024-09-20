package org.ofz.management.dto.api.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserLimitResponse {
    private final int currentLimit;
    private final int totalLimit;
    private final int totalMortgagedPrice;
    private final double mortgagedMaintenanceRate;
    private final int totalPaymentAmount;

    @Builder
    public UserLimitResponse(int currentLimit, double totalLimit, int totalMortgagedPrice, int totalPaymentAmount) {
        this.currentLimit = currentLimit;
        this.totalLimit = (int) totalLimit;
        this.totalMortgagedPrice = totalMortgagedPrice;
        this.mortgagedMaintenanceRate = calculateMortgageMaintenanceRate(currentLimit, totalMortgagedPrice);
        this.totalPaymentAmount = totalPaymentAmount;
    }

    private double calculateMortgageMaintenanceRate(int currentLimit, int totalMortgagedPrice) {
        if (currentLimit == 0) {
            return 0;
        }

        double rate = ((double) totalMortgagedPrice / currentLimit) * 100;
        return Math.round(rate * 100.0) / 100.0;
    }
}
