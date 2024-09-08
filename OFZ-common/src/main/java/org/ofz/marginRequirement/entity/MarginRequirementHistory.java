package org.ofz.marginRequirement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
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

    @Column(name = "margin_requirement", nullable = false)
    private int marginRequirement;

    public MarginRequirementHistory(Long userId, int mortgageSum, int todayLimit, int marginRequirement) {
        this.userId = userId;
        this.mortgageSum = mortgageSum;
        this.todayLimit = todayLimit;
        this.marginRequirement = marginRequirement;
    }

    public void updateValues(int mortgageSum, int todayLimit, int marginRequirement) {
        this.mortgageSum = mortgageSum;
        this.todayLimit = todayLimit;
        this.marginRequirement = marginRequirement;
    }
}