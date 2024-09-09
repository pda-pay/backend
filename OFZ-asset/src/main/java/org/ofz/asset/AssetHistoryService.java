package org.ofz.asset;

import org.ofz.asset.dto.AssetHistoryLast10DaysRes;
import org.ofz.asset.dto.AssetHistoryRateRes;
import org.ofz.asset.entity.AssetHistory;
import org.ofz.asset.repository.AssetHistoryRepository;
import org.ofz.asset.exception.NoDataFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        List<AssetHistory> latestHistories = assetHistoryRepository.findAllLatestByUser();
        if (latestHistories.isEmpty()) {
            return Collections.emptyList();
        }
        return latestHistories;
    }

    public List<AssetHistoryRateRes> getAllByRateOfChangeLessThan(double limit) {
        return assetHistoryRepository.findByMortgageSumRateOfChangeLessThan(limit);
    }


    /**
     * 특정 유저의 mortgage_sum 변동률 조회
     * @param userId 유저 ID
     * @return mortgage_sum 변동률
     */
    public double getMortgageSumRateOfChange(Long userId) {
        List<AssetHistory> recentHistories = assetHistoryRepository.findTop2ByUserIdOrderByCreatedAtDesc(userId);

        // 이전 데이터가 부족한 경우 변동률을 0으로 설정
        if (recentHistories.size() < 2) {
            logger.warn("User ID: {}, 데이터가 부족하여 변동률을 계산할 수 없습니다.", userId);
            return 0.0;
        }

        // 명확하게 최신과 이전 데이터를 구분하기 위해 created_at으로 정렬 확인
        recentHistories.sort((h1, h2) -> h2.getCreatedAt().compareTo(h1.getCreatedAt()));

        AssetHistory latest = recentHistories.get(0);
        AssetHistory previous = recentHistories.get(1);

        logger.info("User ID: {}, 최신 데이터 ID: {}, Created At: {}", userId, latest.getId(), latest.getCreatedAt());
        logger.info("User ID: {}, 이전 데이터 ID: {}, Created At: {}", userId, previous.getId(), previous.getCreatedAt());

        int latestSum = latest.getMortgageSum();
        int previousSum = previous.getMortgageSum();

        // 변동률 계산 및 소수점 첫째 자리 반올림
        double rateOfChange = previousSum == 0 ? 0.0 : ((double) (latestSum - previousSum) / previousSum) * 100;
        rateOfChange = Math.round(rateOfChange * 10.0) / 10.0;

        // 최신 데이터에 변동률 업데이트 및 저장
        latest.updateMortgageSumRateOfChange(rateOfChange);
        assetHistoryRepository.save(latest);
        logger.info("User ID: {}, 최신 데이터에 변동률 {}% 저장 완료", userId, rateOfChange);

        return rateOfChange;
    }

    /**
     * 어제부터 어제-10일 전까지의 데이터 조회
     */
    /**
     * 특정 유저의 어제부터 어제-10일 전까지의 데이터를 조회
     * @param userId 유저 ID
     * @return 어제부터 어제-10일 전까지의 데이터 리스트
     */
    public List<AssetHistoryLast10DaysRes> getLast10DaysData(Long userId) {
        LocalDate endDate = LocalDate.now().minusDays(1); // 어제 날짜
        LocalDate startDate = endDate.minusDays(9); // 어제-10일 전 날짜

        LocalDateTime startDateTime = startDate.atStartOfDay(); // 시작일의 00:00:00
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX); // 종료일의 23:59:59

        List<AssetHistory> histories = assetHistoryRepository.findDataForLast10Days(userId, startDateTime, endDateTime);

        // Map을 사용하여 날짜별로 데이터를 정리
        Map<LocalDate, AssetHistory> historyMap = histories.stream()
                .collect(Collectors.toMap(history -> history.getCreatedAt().toLocalDate(), history -> history));

        List<AssetHistoryLast10DaysRes> fullData = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            AssetHistory history = historyMap.getOrDefault(date, new AssetHistory(null, userId, 0, 0, 0, date.atStartOfDay(), null, 0.0, 0));
            // DTO로 변환하여 리스트에 추가
            fullData.add(new AssetHistoryLast10DaysRes(
                    history.getUserId(),
                    history.getMortgageSum(),
                    history.getTodayLimit(),
                    history.getMaxLimit(),
                    history.getCreatedAt()
            ));
        }

        return fullData;
    }

    /**
     * 모든 유저의 변동률을 계산하고 저장
     */
    public void calculateAndSaveRateOfChangeForAllUsers() {
        List<Long> allUserIds = assetHistoryRepository.findAllUserId();
        for (Long userId : allUserIds) {
            try {
                double rateOfChange = getMortgageSumRateOfChange(userId);
                logger.info("User ID: {}, Rate of Change: {}%", userId, rateOfChange);
            } catch (NoDataFoundException e) {
                logger.warn("User ID: {} 데이터가 부족하여 변동률 계산 불가", userId);
            } catch (Exception e) {
                logger.error("User ID: {} 처리 중 예기치 않은 오류 발생: {}", userId, e.getMessage(), e);
            }
        }
    }
}
