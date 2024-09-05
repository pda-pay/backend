package org.ofz.management.repository;

import org.ofz.management.dto.database.UserStockProjection;
import org.ofz.management.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
