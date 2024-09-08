package org.ofz.marginRequirement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "margin_requirement_history")
public class MarginRequirementHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "margin_requirement", nullable = false)
    private int marginRequirement;

    public MarginRequirementHistory() {}

    public MarginRequirementHistory(Long userId, int marginRequirement) {
        this.userId = userId;
        this.marginRequirement = marginRequirement;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public int getMarginRequirement() {
        return marginRequirement;
    }

    public void changeMarginRequirement(int marginRequirement) {
        this.marginRequirement = marginRequirement;
    }
}
