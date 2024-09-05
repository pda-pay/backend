package org.ofz.management.repository;

import org.ofz.management.StockPriority;
import org.ofz.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockPriorityRepository extends JpaRepository<StockPriority, Long> {
    List<StockPriority> findAllStockPrioritiesByUserId(Long userId);

    Optional<StockPriority> findStockPriorityByStockRankAndAccountNumberAndStockCodeAndUserId(
            int stockRank,
            String accountNumber,
            String stockCode,
            Long userId
    );

    void deleteAllByUserId(Long userId);
}
