package org.ofz.payment.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "Payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "previous_month_debt")
    private int previousMonthDebt;

    @Column(name = "current_month_debt")
    private int currentMonthDebt;

    @Column(name = "credit_limit")
    private int creditLimit;

    @Column(name = "repayment_date")
    private int repaymentDate;

    @Column(name = "rate_flag")
    private Boolean rateFlag;

    @Column(name = "pay_flag")
    private Boolean payFlag;

    @Column(name = "overdue_day")
    private LocalDate overdueDay;

    private int password;

    @Column(name = "repayment_account_id")
    private Long repaymentAccountId;

    public void plusCurrentMonthDebt(int paymentAmount) {
        this.currentMonthDebt += paymentAmount;
    }

    public Payment() {}

    public Payment(Long id, Long userId, int previousMonthDebt, int currentMonthDebt, int creditLimit, int repaymentDate, Boolean rateFlag, Boolean payFlag, LocalDate overdueDay, int password, Long repaymentAccountId) {
        this.id = id;
        this.userId = userId;
        this.previousMonthDebt = previousMonthDebt;
        this.currentMonthDebt = currentMonthDebt;
        this.creditLimit = creditLimit;
        this.repaymentDate = repaymentDate;
        this.rateFlag = rateFlag;
        this.payFlag = payFlag;
        this.overdueDay = overdueDay;
        this.password = password;
        this.repaymentAccountId = repaymentAccountId;
    }
}
