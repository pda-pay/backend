package org.ofz.repayment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface RepaymentHistoryRepository extends JpaRepository<RepaymentHistory, Long> {

    List<RepaymentHistory> findRepaymentHistoriesByCreatedAtBetweenAndUserId(LocalDateTime after, LocalDateTime before, Long userId);
}
