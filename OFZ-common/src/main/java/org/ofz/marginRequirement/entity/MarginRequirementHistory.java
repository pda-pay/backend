package org.ofz.marginRequirement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "margin_requirement_history")
public class MarginRequirementHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "mortgage_sum", nullable = false)
    private int mortgageSum;

    @Column(name = "today_limit", nullable = false)
    private int todayLimit;

    @Column(name = "max_limit", nullable = false)
    private int maxLimit;

    @Column(name = "margin_requirement", nullable = false)
    private int marginRequirement;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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


    public MarginRequirementHistory(Long userId, int mortgageSum, int todayLimit, int maxLimit, int marginRequirement) {
        this.userId = userId;
        this.mortgageSum = mortgageSum;
        this.todayLimit = todayLimit;
        this.maxLimit = maxLimit;
        this.marginRequirement = marginRequirement;
    }

    public void updateValues(int mortgageSum, int todayLimit, int maxLimit, int marginRequirement) {
        this.mortgageSum = mortgageSum;
        this.todayLimit = todayLimit;
        this.maxLimit = maxLimit;
        this.marginRequirement = marginRequirement;
    }
}