package org.ofz.asset.dto;

import java.time.LocalDateTime;

public class AssetHistoryRateRes {

    private Long id;
    private Long userId;
    private LocalDateTime createdAt;
    private double mortgageSumRateOfChange;

    public AssetHistoryRateRes(Long id, Long userId, LocalDateTime createdAt, double mortgageSumRateOfChange) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.mortgageSumRateOfChange = mortgageSumRateOfChange;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getMortgageSumRateOfChange() {
        return mortgageSumRateOfChange;
    }
}
