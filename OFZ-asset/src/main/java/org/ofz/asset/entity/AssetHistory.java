package org.ofz.asset.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "asset_history")
public class AssetHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "mortgage_sum", nullable = false)
    private int mortgageSum;

    @Column(name = "today_limit", nullable = false)
    private int todayLimit;

    @Column(name = "margin_requirement", nullable = false)
    private int marginRequirement;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "mortgage_sum_rate_of_change")
    private double mortgageSumRateOfChange;

    @Column(name = "max_limit", nullable = false)
    private int maxLimit;

    // 생성 시점에 createdAt 설정
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 업데이트 시점에 updatedAt 설정
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateMortgageSumRateOfChange(double rateOfChange) {
        this.mortgageSumRateOfChange = rateOfChange;
    }
}
