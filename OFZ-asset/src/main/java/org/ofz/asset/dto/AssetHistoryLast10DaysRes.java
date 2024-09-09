package org.ofz.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AssetHistoryLast10DaysRes {
    private Long userId;
    private int mortgageSum;
    private int todayLimit;
    private int maxLimit;
    private LocalDate createdAt;

    public AssetHistoryLast10DaysRes(Long userId, int mortgageSum, int todayLimit, int maxLimit, LocalDateTime createdAt) {
        this.userId = userId;
        this.mortgageSum = mortgageSum;
        this.todayLimit = todayLimit;
        this.maxLimit = maxLimit;
        this.createdAt = createdAt.toLocalDate();
    }
}
