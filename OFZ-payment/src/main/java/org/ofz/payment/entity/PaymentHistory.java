package org.ofz.payment.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "Payment_history")
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_amount")
    private int paymentAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "franchise_id")
    private Franchise franchise;

    @Column(name = "user_id")
    private Long userId;

    public PaymentHistory() {}

    @Builder
    public PaymentHistory(Long id, int paymentAmount, LocalDateTime createdAt, Franchise franchise, Long userId) {
        this.id = id;
        this.paymentAmount = paymentAmount;
        this.createdAt = createdAt;
        this.franchise = franchise;
        this.userId = userId;
    }
}
