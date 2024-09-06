package org.ofz.management.repository;

import org.ofz.management.projection.UserStockProjection;
import org.ofz.management.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Query("SELECT s.accountNumber AS accountNumber, " +
            "s.quantity AS quantity, " +
            "COALESCE(ms.quantity, 0) AS mortgagedQuantity, " +
            "s.stockCode AS stockCode, " +
            "s.companyCode AS companyCode " +
            "FROM Stock s " +
            "LEFT JOIN MortgagedStock ms ON s.accountNumber = ms.accountNumber AND s.stockCode = ms.stockCode " +
            "WHERE s.user.id = :userId")
    List<UserStockProjection> findUserStocksByUserId(@Param("userId") Long userId);

    Optional<Stock> findStockByAccountNumberAndStockCode(String accountNumber, String StockCode);

    @Modifying
    @Query("UPDATE Stock s SET s.quantity = s.quantity - :quantityToSell " +
            "WHERE s.accountNumber = :accountNumber AND s.stockCode = :stockCode AND s.user.id = :userId")
    void reduceQuantity(@Param("accountNumber") String accountNumber,
                        @Param("stockCode") String stockCode,
                        @Param("quantityToSell") int quantityToSell,
                        @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Stock s " +
            "WHERE s.accountNumber = :accountNumber AND s.stockCode = :stockCode AND s.quantity <= 0 AND s.user.id = :userId")
    void deleteIfQuantityZero(@Param("accountNumber") String accountNumber,
                              @Param("stockCode") String stockCode,
                              @Param("userId") Long userId);
}
