package org.ofz.management.repository;

import org.ofz.management.MortgagedStock;
import org.ofz.repayment.dto.projection.QuantityAndStockCodeOfMortgagedStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MortgagedStockRepository extends JpaRepository<MortgagedStock, Long> {
    List<MortgagedStock> findAllMortgagedStocksByUserId(Long userId);

    Optional<MortgagedStock> findMortgagedStockByAccountNumberAndStockCodeAndUserId(String accountNumber, String stockCode, Long userId);

    void deleteAllByUserId(Long userId);

    boolean existsByUserId(Long userId);

    List<MortgagedStock> findMortgagedStocksByUserIdOrderByStockCode(Long userId);

    List<QuantityAndStockCodeOfMortgagedStock> findMortgagedStocksByUserId(Long userId);
}
