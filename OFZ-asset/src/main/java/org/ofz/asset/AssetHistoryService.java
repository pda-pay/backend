package org.ofz.asset;

import org.ofz.asset.dto.AssetHistoryLast10DaysRes;
import org.ofz.asset.dto.AssetHistoryRateRes;
import org.ofz.asset.entity.AssetHistory;
import org.ofz.asset.exception.DatabaseAccessException;
import org.ofz.asset.exception.GenericServiceException;
import org.ofz.asset.repository.AssetHistoryRepository;
import org.ofz.rabbitMQ.rabbitDto.AssetMqDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AssetHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(AssetHistoryService.class);

    private final AssetHistoryRepository assetHistoryRepository;

    @Autowired
    public AssetHistoryService(AssetHistoryRepository assetHistoryRepository) {
        this.assetHistoryRepository = assetHistoryRepository;
    }

    /**
     * 각 유저의 최신 margin_requirement 변동률 조회
     */
    public List<AssetHistory> getAllLatestUserMarginRequirements() {
        try {
            List<AssetHistory> latestHistories = assetHistoryRepository.findAllLatestByUser();
            if (latestHistories.isEmpty()) {
                return Collections.emptyList();
            }
            return latestHistories;
        } catch (DataAccessException e) {
            logger.error("데이터베이스 접근 오류: {}", e.getMessage(), e);
            throw new DatabaseAccessException("데이터베이스 접근 중 문제가 발생했습니다.", e);
        }
    }


    /**
     * 담보총액 변동율이 특정 값 이하인 유저 조회
     */
    public List<AssetHistoryRateRes> getAllByRateOfChangeLessThan(double limit) {
        // Step 1: limit 매개변수를 정의합니다
        double rateOfChangeLimit = limit;  // limit 매개변수의 값을 설정합니다

        // Step 2: limit 값을 확인하기 위해 로그를 남깁니다
        logger.info("담보총액 변동률이 {} 이하인 Asset History를 조회합니다.", rateOfChangeLimit);

        // Step 3: 레포지토리 메소드를 실행하여 데이터를 조회합니다
        List<AssetHistoryRateRes> assetHistoryList = assetHistoryRepository.findByMortgageSumRateOfChangeLessThan(rateOfChangeLimit);

        // Step 4: 결과 리스트가 비어있는지 확인합니다
        if (assetHistoryList.isEmpty()) {
            logger.warn("담보총액 변동률이 {} 이하인 기록이 없습니다.", rateOfChangeLimit);
        } else {
            logger.info("담보총액 변동률이 {} 이하인 기록 {}개를 찾았습니다.", rateOfChangeLimit, assetHistoryList.size());
        }

        // Step 5: 조회된 리스트를 반환합니다
        return assetHistoryList;


    }


    /**
     * 특정 유저의 mortgage_sum 변동률 조회
     * @param userId 유저 ID
     * @return mortgage_sum 변동률
     */
    @Transactional
    public double getMortgageSumRateOfChange(Long userId) {
        try {
            List<AssetHistory> recentHistories = assetHistoryRepository.findTop2ByUserIdOrderByCreatedAtDesc(userId);

            if (recentHistories.size() < 2) {
                logger.warn("User ID: {}, 데이터가 부족하여 변동률을 계산할 수 없습니다.", userId);
                return 0.0;
            }

            recentHistories.sort((h1, h2) -> h2.getCreatedAt().compareTo(h1.getCreatedAt()));

            AssetHistory latest = recentHistories.get(0);
            AssetHistory previous = recentHistories.get(1);

            logger.info("User ID: {}, 최신 데이터 ID: {}, Created At: {}", userId, latest.getId(), latest.getCreatedAt());
            logger.info("User ID: {}, 이전 데이터 ID: {}, Created At: {}", userId, previous.getId(), previous.getCreatedAt());

            int latestSum = latest.getMortgageSum();
            int previousSum = previous.getMortgageSum();

            double rateOfChange = previousSum == 0 ? 0.0 : ((double) (latestSum - previousSum) / previousSum) * 100;
            rateOfChange = Math.round(rateOfChange * 10.0) / 10.0;

            latest.updateMortgageSumRateOfChange(rateOfChange);
            assetHistoryRepository.save(latest);
            logger.info("User ID: {}, 최신 데이터에 변동률 {}% 저장 완료", userId, rateOfChange);

            return rateOfChange;
        } catch (DataAccessException e) {
            logger.error("데이터베이스 접근 오류: {}", e.getMessage(), e);
            throw new DatabaseAccessException("데이터베이스 접근 중 문제가 발생했습니다.", e);
        } catch (Exception e) {
            logger.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new GenericServiceException("서버 내부 오류가 발생했습니다.", e);
        }
    }

    /**
     * 특정 유저의 어제부터 어제-10일 전까지의 데이터를 조회
     * @param userId 유저 ID
     * @return 어제부터 어제-10일 전까지의 데이터 리스트
     */
    @Transactional
    public List<AssetHistoryLast10DaysRes> getLast10DaysData(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(9);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        try {
            List<AssetHistory> histories = assetHistoryRepository.findDataForLast10Days(userId, startDateTime, endDateTime);

            Map<LocalDate, AssetHistory> historyMap = histories.stream()
                    .collect(Collectors.toMap(history -> history.getCreatedAt().toLocalDate(), history -> history));

            List<AssetHistoryLast10DaysRes> fullData = new ArrayList<>();
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                AssetHistory history = historyMap.getOrDefault(date, new AssetHistory(null, userId, 0, 0, 0, date.atStartOfDay(), null, 0.0, 0));

                LocalDateTime adjustedCreatedAt = history.getCreatedAt().minusDays(1);

                fullData.add(new AssetHistoryLast10DaysRes(
                        history.getUserId(),
                        history.getMortgageSum(),
                        history.getTodayLimit(),
                        history.getMaxLimit(),
                        adjustedCreatedAt
                ));
            }

            return fullData;
        } catch (DataAccessException e) {
            logger.error("데이터베이스 접근 오류: {}", e.getMessage(), e);
            throw new DatabaseAccessException("데이터베이스 접근 중 문제가 발생했습니다.", e);
        }
    }

    /**
     * 모든 유저의 변동률을 계산하고 저장
     */
    @Transactional
    public void calculateAndSaveRateOfChangeForAllUsers() {
        List<Long> allUserIds = assetHistoryRepository.findAllUserId();
        for (Long userId : allUserIds) {
            try {
                double rateOfChange = getMortgageSumRateOfChange(userId);
                logger.info("User ID: {}, Rate of Change: {}%", userId, rateOfChange);
            } catch (DataAccessException e) {
                logger.error("데이터베이스 접근 오류: {}", e.getMessage(), e);
                throw new DatabaseAccessException("데이터베이스 접근 중 문제가 발생했습니다.", e);
            } catch (Exception e) {
                logger.error("User ID: {} 처리 중 예기치 않은 오류 발생: {}", userId, e.getMessage(), e);
                throw new GenericServiceException("서버 내부 오류가 발생했습니다.", e);
            }
        }
    }

    /**
     * 메시지 큐로부터 AssetMqDTO 수신하여 AssetHistory 엔티티에 저장
     * @param assetMqDTO 메시지 큐로부터 수신한 DTO
     */
    @Transactional
    public void saveAssetHistoryFromMq(AssetMqDTO assetMqDTO) {
        try {
            AssetHistory assetHistory = new AssetHistory(
                    null,
                    assetMqDTO.getUserId(),
                    assetMqDTO.getMortgageSum(),
                    assetMqDTO.getTodayLimit(),
                    assetMqDTO.getMargin_requirement(),
                    LocalDateTime.now(),
                    null,
                    0.0, // 변동률은 초기값 0으로 설정
                    assetMqDTO.getMaxLimit()
            );
            assetHistoryRepository.save(assetHistory);
            calculateAndSaveRateOfChangeForAllUsers();
            logger.info("Asset history for user ID: {} saved successfully.", assetMqDTO.getUserId());
        } catch (Exception e) {
            logger.error("Error saving asset history from MQ: {}", e.getMessage(), e);
        }
    }
}
