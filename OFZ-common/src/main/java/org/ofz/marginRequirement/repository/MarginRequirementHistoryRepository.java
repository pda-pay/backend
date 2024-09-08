package org.ofz.marginRequirement.repository;

import org.ofz.marginRequirement.entity.MarginRequirementHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MarginRequirementHistoryRepository extends JpaRepository<MarginRequirementHistory, Long> {
    Optional<MarginRequirementHistory> findByUserId(Long userId);
}
