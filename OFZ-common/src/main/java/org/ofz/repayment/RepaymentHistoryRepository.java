package org.ofz.repayment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepaymentHistoryRepository extends JpaRepository<RepaymentHistory, Long> {

    List<AmountAndDateAndTypeProjection> findRepaymentHistoriesByUserIdOrderByIdAsc(Long userId);
}
