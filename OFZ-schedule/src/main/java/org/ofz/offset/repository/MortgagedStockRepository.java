package org.ofz.offset.repository;

import org.ofz.management.MortgagedStock;
import org.ofz.offset.projection.MortgagedStockProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MortgagedStockRepository extends JpaRepository<MortgagedStock, Long> {
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
            "ORDER BY m.stockCode ASC")
    List<MortgagedStockProjection> sortNonPriorityMortgage(@Param("userId") Long userId);
}
