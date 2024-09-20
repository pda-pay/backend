package org.ofz.management.repository;

import org.ofz.management.entity.StockInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StockInformationRepository extends JpaRepository<StockInformation, Integer> {
    Optional<StockInformation> findByStockCode(String stockCode);
    @Query("SELECT s.stockCode FROM StockInformation s")
    List<String> findAllStockCodes();
}
