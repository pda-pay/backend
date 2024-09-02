package org.ofz.management.repository;

import org.ofz.management.entity.StockInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockInformationRepository extends JpaRepository<StockInformation, Integer> {
    Optional<StockInformation> findByStockCode(String stockCode);
}
