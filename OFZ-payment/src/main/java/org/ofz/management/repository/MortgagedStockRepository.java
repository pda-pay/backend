package org.ofz.management.repository;

import org.ofz.management.entity.MortgagedStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MortgagedStockRepository extends JpaRepository<MortgagedStock, Long> {
    List<MortgagedStock> findAllMortgagedStocksByUserId(Long userId);
}
