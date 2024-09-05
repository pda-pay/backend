package org.ofz.management.repository;

import org.ofz.management.MortgagedStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MortgagedStockRepository extends JpaRepository<MortgagedStock, Long> {
    List<MortgagedStock> findAllMortgagedStocksByUserId(Long userId);

    Optional<MortgagedStock> findMortgagedStockByAccountNumberAndStockCode(String accountNumber, String stockCode);

    void deleteAllByUserId(Long userId);
}
