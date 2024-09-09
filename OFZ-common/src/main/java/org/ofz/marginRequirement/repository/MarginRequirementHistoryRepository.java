package org.ofz.marginRequirement.repository;

import org.ofz.marginRequirement.entity.MarginRequirementHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarginRequirementHistoryRepository extends JpaRepository<MarginRequirementHistory, Long> {
    @Query("SELECT m FROM MarginRequirementHistory m WHERE m.userId = :userId")
    Optional<MarginRequirementHistory> findByUserId(@Param("userId") Long userId);

    List<MarginRequirementHistory> findByMarginRequirementLessThanEqual(int maxRequirement);

}
