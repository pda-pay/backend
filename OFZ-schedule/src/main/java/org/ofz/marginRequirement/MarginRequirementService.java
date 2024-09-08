package org.ofz.marginRequirement;

import org.ofz.management.entity.StockInformation;
import org.ofz.management.repository.MortgagedStockRepository;
import org.ofz.management.repository.StockInformationRepository;
import org.ofz.management.utils.StockStability;
import org.ofz.marginRequirement.entity.MarginRequirementHistory;
import org.ofz.marginRequirement.exception.DatabaseAccessException;
import org.ofz.marginRequirement.exception.GenericServiceException;
import org.ofz.marginRequirement.exception.PriceNotFoundException;
import org.ofz.marginRequirement.exception.StockInformationNotFoundException;
import org.ofz.marginRequirement.repository.MarginRequirementHistoryRepository;
import org.ofz.payment.Payment;
import org.ofz.payment.PaymentRepository;
import org.ofz.management.entity.MortgagedStock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ofz.redis.RedisUtil;
import org.springframework.dao.DataAccessException;

import java.util.Collections;
import java.util.List;

@Service
public class MarginRequirementService {

    private static final Logger logger = LoggerFactory.getLogger(MarginRequirementService.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MortgagedStockRepository mortgagedStockRepository;

    @Autowired
    private StockInformationRepository stockInformationRepository;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MarginRequirementHistoryRepository marginRequirementHistoryRepository;

    // 전체 유저의 margin requirement 조회 메소드
    public List<MarginRequirementHistory> getAllUserMarginRequirements() {
        return marginRequirementHistoryRepository.findAll();
    }

    // margin_requirement가 limit 이하인 유저 조회
    public List<MarginRequirementHistory> getUsersWithMarginRequirementUnder(int limit) {
        try {
            List<MarginRequirementHistory> results = marginRequirementHistoryRepository.findByMarginRequirementLessThanEqual(limit);
            if (results == null) {
                logger.warn("쿼리 결과가 null입니다. 빈 리스트를 반환합니다.");
                return Collections.emptyList();
            }
            return results;
        } catch (DataAccessException e) {
            logger.error("데이터베이스 접근 중 오류 발생: {}", e.getMessage(), e);
            throw new DatabaseAccessException("데이터베이스 오류: 데이터 조회 중 문제가 발생했습니다.", e);
        } catch (Exception e) {
            logger.error("예기치 못한 오류가 발생했습니다: {}", e.getMessage(), e);
            throw new GenericServiceException("서버 내부 오류: 예기치 못한 문제가 발생했습니다. 잠시 후 다시 시도해주세요.", e);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 실행
    @Transactional
    public void processAllUserLimits() {
        logger.info("모든 유저의 담보 한도 비율 계산을 시작합니다.");

        List<Payment> allPayments = paymentRepository.findAll();

        for (Payment payment : allPayments) {
            final Long userId = payment.getUser().getId();
            try {
                List<MortgagedStock> mortgagedStocks = mortgagedStockRepository.findByUserId(userId);

                if (mortgagedStocks == null || mortgagedStocks.isEmpty()) {
                    logger.warn("유저 ID: {}에 대해 담보된 주식이 없습니다.", userId);
                    continue;
                }

                // 담보 총액 계산
                int mortgageSum = mortgagedStocks.stream()
                        .mapToInt(stock -> {
                            Integer price = redisUtil.fetchStoredPreviousPrice(stock.getStockCode());
                            if (price == null) {
                                throw new PriceNotFoundException("유저 ID: " + userId + ", 주식 코드: " + stock.getStockCode() + "에 대한 가격 정보를 찾을 수 없습니다.");
                            }
                            return stock.getQuantity() * price;
                        })
                        .sum();

                // 최대 한도 계산
                double maxLimit = mortgagedStocks.stream()
                        .mapToDouble(stock -> {
                            String stockCode = stock.getStockCode();
                            Integer stockPrice = redisUtil.fetchStoredPreviousPrice(stockCode);
                            if (stockPrice == null) {
                                throw new PriceNotFoundException("주식 코드: " + stockCode + "의 가격 정보를 찾을 수 없습니다.");
                            }
                            StockInformation stockInformation = stockInformationRepository.findByStockCode(stockCode)
                                    .orElseThrow(() -> new StockInformationNotFoundException("Stock information not found for stockCode: " + stockCode));
                            return StockStability.calculateLimitPrice(stockInformation.getStabilityLevel(), stockPrice) * stock.getQuantity();
                        })
                        .sum();

                // 현재 한도 비율 계산
                final int creditLimit = payment.getCreditLimit();
                double currentLimitRatio;
                int marginRequirement;
                if (creditLimit == 0) {
                    logger.error("유저 ID: {}, 크레딧 한도가 0이므로 margin_requirement를 -1로 설정합니다.", userId);
                    marginRequirement = -1;
                    currentLimitRatio = 0;
                } else {
                    currentLimitRatio = (mortgageSum / creditLimit) * 100;
                    marginRequirement = (int) Math.floor(currentLimitRatio);
                }

                // 기존의 MarginRequirementHistory 찾기 또는 새로 생성
                MarginRequirementHistory history = marginRequirementHistoryRepository.findByUserId(userId)
                        .orElseGet(() -> new MarginRequirementHistory(userId, mortgageSum, creditLimit, marginRequirement));

                // mortgageSum, currentLimit 및 margin_requirement 값 업데이트
                history.updateValues(mortgageSum, creditLimit, marginRequirement);

                // 최대 한도 비율 계산
                if (maxLimit == 0) {
                    logger.error("유저 ID: {}, 최대 한도가 0입니다.", userId);
                    continue;
                }
                double maxLimitRatio = (mortgageSum / maxLimit) * 100;

                logger.info("유저 ID: {}, 현재 한도 비율: {}, 최대 한도 비율: {}", userId, currentLimitRatio, maxLimitRatio);

                // 140% 이하인지 확인하여 플래그 변경
                if (currentLimitRatio <= 140) {
                    payment.disableRateFlag();
                    logger.info("유저 ID: {}, 현재 한도 비율이 140% 이하이므로 rateFlag를 false로 설정합니다.", userId);
                } else {
                    payment.enableRateFlag();
                    logger.info("유저 ID: {}, 현재 한도 비율이 140% 초과이므로 rateFlag를 true로 설정합니다.", userId);
                }

                marginRequirementHistoryRepository.save(history);
                paymentRepository.save(payment);

            } catch (PriceNotFoundException | StockInformationNotFoundException e) {
                logger.error("유저 ID: {}의 한도 비율 계산 중 오류 발생: {}", userId, e.getMessage(), e);
            } catch (ArithmeticException e) {
                logger.error("유저 ID: {}의 계산 중 나누기 오류 발생: {}", userId, e.getMessage(), e);
            } catch (Exception e) {
                logger.error("유저 ID: {}의 한도 비율 계산 중 알 수 없는 오류 발생: {}", userId, e.getMessage(), e);
            }
        }

        logger.info("모든 유저의 담보 한도 비율 계산이 완료되었습니다.");
    }
}
