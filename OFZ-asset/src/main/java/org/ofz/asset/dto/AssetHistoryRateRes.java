package org.ofz.asset.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class AssetHistoryRateRes {

    private Long id;
    private Long userId;
    private String createdAt;
    private double mortgageSumRateOfChange;


    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AssetHistoryRateRes(Long id, Long userId, LocalDateTime createdAt, double mortgageSumRateOfChange) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt.format(DATE_FORMATTER);
        this.mortgageSumRateOfChange = mortgageSumRateOfChange;
    }

}
