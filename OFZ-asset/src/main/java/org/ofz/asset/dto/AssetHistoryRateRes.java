package org.ofz.asset.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class AssetHistoryRateRes {

    private Long id;
    private Long userId;
    private double mortgageSumRateOfChange;

    public AssetHistoryRateRes(Long id, Long userId, double mortgageSumRateOfChange) {
        this.id = id;
        this.userId = userId;
        this.mortgageSumRateOfChange = mortgageSumRateOfChange;
    }

}
