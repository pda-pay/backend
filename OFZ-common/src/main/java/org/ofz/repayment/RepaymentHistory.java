package org.ofz.repayment;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@Table(name = "Repayment_history")
public class RepaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "repayment_amount")
    private int repaymentAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    private RepaymentType type;

    public RepaymentHistory() {}

    public RepaymentHistory(Long id, int repaymentAmount, LocalDateTime createdAt, Long userId, RepaymentType type) {
        this.id = id;
        this.repaymentAmount = repaymentAmount;
        this.createdAt = createdAt;
        this.userId = userId;
        this.type = type;
    }
}
