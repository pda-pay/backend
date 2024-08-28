package org.ofz.payment.repository;

import org.ofz.payment.entity.Franchise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FranchiseRepository extends JpaRepository<Franchise, Long> {
    Optional<Franchise> findFranchiseByCode(int franchiseCode);
}
