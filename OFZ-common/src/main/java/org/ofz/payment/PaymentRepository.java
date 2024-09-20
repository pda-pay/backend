package org.ofz.payment;

import org.ofz.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findPaymentByUserId(Long userId);
    @Query("SELECT p.creditLimit FROM Payment p WHERE p.user.id = :userId")
    Optional<Integer> findCreditLimitByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(p.previousMonthDebt + p.currentMonthDebt), 0) FROM Payment p WHERE p.user.id = :userId")
    int findTotalDebtByUserId(@Param("userId") Long userId);

    @Query("SELECT p " +
            "FROM Payment p " +
            "WHERE p.repaymentDate = :repaymentDate " +
            "OR (p.overdueDay IS NOT NULL AND FUNCTION('DATEDIFF', CURRENT_DATE, p.overdueDay) <= 2)")
    List<Payment> findByRepaymentDateOrOverdueDay(@Param("repaymentDate") int repaymentDate);

//    @Query(value = "SELECT p.user_id FROM Payment p WHERE p.overdue_day IS NOT NULL AND DATEDIFF(CURRENT_DATE, p.overdue_day) >= 3", nativeQuery = true)
//    List<Long> findByOverdueDay();
    @Query("SELECT p " +
            "FROM Payment p " +
            "WHERE p.overdueDay IS NOT NULL AND FUNCTION('DATEDIFF', CURRENT_DATE, p.overdueDay) >= 3")
    List<Payment> findByOverdueDay();
}
