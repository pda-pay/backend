package org.ofz.management.repository;

import org.ofz.management.entity.MortgagedStock;
import org.ofz.management.projection.MortgagedStockProjection;
import org.ofz.management.projection.QuantityAndStockCodeOfMortgagedStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MortgagedStockRepository extends JpaRepository<MortgagedStock, Long> {
    List<MortgagedStock> findAllMortgagedStocksByUserId(Long userId);

    Optional<MortgagedStock> findMortgagedStockByAccountNumberAndStockCodeAndUserId(String accountNumber, String stockCode, Long userId);

    @Query("SELECT p.stockCode AS stockCode, p.quantity AS quantity, p.accountNumber AS accountNumber " +
            "FROM StockPriority p " +
            "WHERE p.user.id = :userId " +
            "ORDER BY p.stockRank ASC")
    List<MortgagedStockProjection> sortPriorityMortgage(@Param("userId") Long userId);

    @Query("SELECT m.stockCode AS stockCode, " +
            "(m.quantity - COALESCE(sp.quantity, 0)) AS quantity, " +
            "m.accountNumber AS accountNumber " +
            "FROM MortgagedStock m " +
            "LEFT JOIN StockPriority sp ON m.stockCode = sp.stockCode AND m.accountNumber = sp.accountNumber AND sp.user.id = :userId " +
            "WHERE m.user.id = :userId " +
            "AND (m.quantity - COALESCE(sp.quantity, 0)) > 0 " +
            "ORDER BY m.stockCode ASC")
    List<MortgagedStockProjection> sortNonPriorityMortgage(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE MortgagedStock m SET m.quantity = m.quantity - :quantityToSell " +
            "WHERE m.accountNumber = :accountNumber AND m.stockCode = :stockCode AND m.user.id = :userId")
    void reduceQuantity(@Param("accountNumber") String accountNumber,
                        @Param("stockCode") String stockCode,
                        @Param("quantityToSell") int quantityToSell,
                        @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM MortgagedStock m " +
            "WHERE m.accountNumber = :accountNumber AND m.stockCode = :stockCode AND m.quantity <= 0 AND m.user.id = :userId")
    void deleteIfQuantityZero(@Param("accountNumber") String accountNumber,
                              @Param("stockCode") String stockCode,
                              @Param("userId") Long userId);

    List<MortgagedStock> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    List<MortgagedStock> findMortgagedStocksByUserIdOrderByStockCode(Long userId);

    List<QuantityAndStockCodeOfMortgagedStock> findMortgagedStocksByUserId(Long userId);
}
