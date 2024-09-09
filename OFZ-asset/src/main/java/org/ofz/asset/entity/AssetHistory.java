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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "mortgage_sum_rate_of_change")
    private double mortgageSumRateOfChange;

    public void updateMortgageSumRateOfChange(double rateOfChange) {
        this.mortgageSumRateOfChange = rateOfChange;
    }
}
