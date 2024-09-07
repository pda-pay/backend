package org.ofz.management.repository;

import org.ofz.management.StockPriority;
import org.ofz.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    boolean existsByUserId(Long userId);

    List<StockPriority> findStockPrioritiesByUserIdOrderByStockRank(Long userId);

    @Query("SELECT SUM(sp.quantity) AS sum FROM StockPriority sp " +
            "WHERE sp.user.id = :userId AND sp.accountNumber = :accountNumber " +
            "AND sp.stockCode = :stockCode")
    Optional<Integer> findStockPriorityQuantityByUserIdAndAccountNumberAndStockCode(Long userId, String accountNumber, String stockCode);
}
