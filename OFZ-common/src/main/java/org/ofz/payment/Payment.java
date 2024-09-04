package org.ofz.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.ofz.user.User;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "Payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "previous_month_debt")
    private int previousMonthDebt;

    @Column(name = "current_month_debt")
    private int currentMonthDebt;

    @Column(name = "credit_limit")
    private int creditLimit;

    @Column(name = "repayment_date")
    private int repaymentDate;

    @Column(name = "rate_flag")
    private boolean rateFlag;

    @Column(name = "pay_flag")
    private boolean payFlag;

    @Column(name = "overdue_day")
    private LocalDate overdueDay;

    private String password;

    @Column(name = "repayment_account_number")
    private String repaymentAccountNumber;

    public void plusCurrentMonthDebt(int paymentAmount) {
        this.currentMonthDebt += paymentAmount;
    }

    public void minusPreviousMonthDebt(int prepaymentAmount) {
        this.previousMonthDebt -= prepaymentAmount;
    }

    public void minusCurrentMonthDebt(int prepaymentAmount) {
        this.currentMonthDebt -= prepaymentAmount;
    }

    public Payment() {}

    @Builder
    public Payment(User user, int creditLimit, int repaymentDate, String password, String repaymentAccountNumber,LocalDate overdueDay, int previousMonthDebt, int currentMonthDebt) {
        this.user = user;
        this.creditLimit = creditLimit;
        this.repaymentDate = repaymentDate;
        this.password = password;
        this.repaymentAccountNumber = repaymentAccountNumber;
        this.rateFlag = true;
        this.payFlag = true;
        this.overdueDay = overdueDay;
        this.previousMonthDebt = previousMonthDebt;
        this.currentMonthDebt = currentMonthDebt;
    }

    public void changeCreditLimit(int creditLimit) {
        this.creditLimit = creditLimit;
    }

    public void changeRepaymentAccountNumber(String repaymentAccountNumber) {
        this.repaymentAccountNumber = repaymentAccountNumber;
    }

    public void changeRepaymentDate(int repaymentDate) {
        this.repaymentDate = repaymentDate;
    }

    public void updateOverdueDay(LocalDate now) {
        this.overdueDay=now;
    }

    public void disablePay() {
        payFlag = false;
    }

    public void enablePay() {
        payFlag = true;
    }
}
