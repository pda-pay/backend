package org.ofz.asset.repository;

import org.ofz.asset.dto.AssetHistoryRateRes;
import org.ofz.asset.entity.AssetHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Long> {

    // 전일과 오늘의 데이터 가져오기
    @Query(value = "SELECT * FROM asset_history WHERE user_id = :userId " +
            "AND created_at >= (SELECT MIN(created_at) FROM " +
            "(SELECT created_at FROM asset_history WHERE user_id = :userId ORDER BY created_at DESC LIMIT 2) AS subquery)",
            nativeQuery = true)
    List<AssetHistory> findTop2ByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    // 전체 유저 id 조회
     @Query("SELECT DISTINCT ah.userId FROM AssetHistory ah")
    List<Long> findAllUserId();

    // 각 유저의 최신 데이터만 조회
    @Query(value = "SELECT * FROM asset_history ah " +
            "WHERE ah.created_at = (" +
            "SELECT MAX(ah2.created_at) " +
            "FROM asset_history ah2 " +
            "WHERE ah2.user_id = ah.user_id AND DATE(ah2.created_at) = :targetDate)",
            nativeQuery = true)
    List<AssetHistory> findAllLatestByUser(@Param("targetDate") String targetDate);


    // 담보총액 변동율이 특정 값 이하인 유저 조회
    @Query("SELECT new org.ofz.asset.dto.AssetHistoryRateRes(ah.id, ah.userId, ah.mortgageSum, ah.todayLimit, ah.marginRequirement, ah.mortgageSumRateOfChange) " +
            "FROM AssetHistory ah " +
            "WHERE ah.id IN (" +
            "    SELECT MAX(subAh.id) " +
            "    FROM AssetHistory subAh " +
            "    WHERE FUNCTION('DATE', subAh.createdAt) = :targetDate " +
            "    AND subAh.mortgageSumRateOfChange <= :limit " +
            "    GROUP BY subAh.userId" +
            ")")
    List<AssetHistoryRateRes> findByMortgageSumRateOfChangeLessThan(@Param("limit") double limit, @Param("targetDate") String targetDate);

    // 어제부터 어제-10일 전까지의 데이터를 가져오는 쿼리
    @Query("SELECT ah FROM AssetHistory ah WHERE ah.userId = :userId " +
            "AND ah.createdAt BETWEEN :startDate AND :endDate " +
            "ORDER BY ah.createdAt DESC")
    List<AssetHistory> findDataForLast10Days(@Param("userId") Long userId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

}

