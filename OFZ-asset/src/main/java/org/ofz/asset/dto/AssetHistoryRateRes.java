package org.ofz.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssetHistoryRateRes {
    private Long id;
    private Long userId;
    private int mortgageSum;
    private int todayLimit;
    private int marginRequirement;
    private double mortgageSumRateOfChange;

    public AssetHistoryRateRes(Long id, Long userId, int mortgageSum, int todayLimit, int marginRequirement) {
        this.id = id;
        this.userId = userId;
        this.mortgageSum = mortgageSum;
        this.todayLimit = todayLimit;
        this.marginRequirement = marginRequirement;
    }
}
