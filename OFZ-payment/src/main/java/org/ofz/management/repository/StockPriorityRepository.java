package org.ofz.management.repository;

import org.ofz.management.StockPriority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockPriorityRepository extends JpaRepository<StockPriority, Long> {
    List<StockPriority> findAllStockPrioritiesByUserId(Long userId);
}
