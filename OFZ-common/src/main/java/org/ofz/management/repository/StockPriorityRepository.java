package org.ofz.management.repository;

import org.ofz.management.entity.StockPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Modifying
    @Query("UPDATE StockPriority s SET s.quantity = s.quantity - :quantityToSell " +
            "WHERE s.accountNumber = :accountNumber AND s.stockCode = :stockCode AND s.user.id = :userId")
    void reduceQuantity(@Param("accountNumber") String accountNumber,
                        @Param("stockCode") String stockCode,
                        @Param("quantityToSell") int quantityToSell,
                        @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM StockPriority s " +
            "WHERE s.accountNumber = :accountNumber AND s.stockCode = :stockCode AND s.quantity <= 0 AND s.user.id = :userId")
    void deleteIfQuantityZero(@Param("accountNumber") String accountNumber,
                              @Param("stockCode") String stockCode,
                              @Param("userId") Long userId);

    boolean existsByUserId(Long userId);

    List<StockPriority> findStockPrioritiesByUserIdOrderByStockRank(Long userId);

    @Query("SELECT SUM(sp.quantity) AS sum FROM StockPriority sp " +
            "WHERE sp.user.id = :userId AND sp.accountNumber = :accountNumber " +
            "AND sp.stockCode = :stockCode")
    Optional<Integer> findStockPriorityQuantityByUserIdAndAccountNumberAndStockCode(Long userId, String accountNumber, String stockCode);
}
