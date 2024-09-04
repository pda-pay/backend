package org.ofz.payment;

import org.ofz.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findPaymentByUserId(Long userId);
    @Query("SELECT p.creditLimit FROM Payment p WHERE p.user.id = :userId")
    Optional<Integer> findCreditLimitByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(p.previousMonthDebt + p.currentMonthDebt), 0) FROM Payment p WHERE p.user.id = :userId")
    int findTotalDebtByUserId(@Param("userId") Long userId);
}
