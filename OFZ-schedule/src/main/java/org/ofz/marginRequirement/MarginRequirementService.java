package org.ofz.marginRequirement;

import org.ofz.management.entity.StockInformation;
import org.ofz.management.repository.MortgagedStockRepository;
import org.ofz.management.repository.StockInformationRepository;
import org.ofz.management.utils.StockStability;
import org.ofz.marginRequirement.entity.MarginRequirementHistory;
import org.ofz.marginRequirement.exception.*;
import org.ofz.marginRequirement.repository.MarginRequirementHistoryRepository;
import org.ofz.payment.Payment;
import org.ofz.payment.PaymentRepository;
import org.ofz.management.entity.MortgagedStock;
import org.ofz.rabbitMQ.NotificationType;
import org.ofz.rabbitMQ.Publisher;
import org.ofz.rabbitMQ.rabbitDto.AssetMqDTO;
import org.ofz.rabbitMQ.rabbitDto.MarginRequirementLogDto;
import org.ofz.rabbitMQ.rabbitDto.NotificationMessage;
import org.ofz.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ofz.redis.RedisUtil;
import org.springframework.dao.DataAccessException;
import reactor.netty.udp.UdpServer;

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

    private Publisher<AssetMqDTO> publisher;
    private Publisher<NotificationMessage> notificationPublisher;

    private Publisher<MarginRequirementLogDto> marginRequirementLogDtoPublisher;

    @Autowired
    public MarginRequirementService(Publisher<AssetMqDTO> publisher, Publisher<NotificationMessage> notificationPublisher, Publisher<MarginRequirementLogDto> marginRequirementLogDtoPublisher) {
        this.publisher = publisher;
        this.notificationPublisher = notificationPublisher;
        this.marginRequirementLogDtoPublisher = marginRequirementLogDtoPublisher;
    }

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
            String loginId = payment.getUser().getLoginId(); // 사용자의 로그인 ID를 가져옵니다.

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
                double maxLimitDouble = mortgagedStocks.stream()
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

                int maxLimit = (int) Math.floor(maxLimitDouble);

                // 현재 한도 비율 계산
                final int creditLimit = payment.getCreditLimit();
                double currentLimitRatio;
                int marginRequirement;
                if (creditLimit == 0) {
                    logger.error("유저 ID: {}, 크레딧 한도가 0이므로 margin_requirement를 -1로 설정합니다.", userId);
                    marginRequirement = -1;
                    currentLimitRatio = 0;
                } else {
                    currentLimitRatio = ((double) mortgageSum / creditLimit) * 100;
                    marginRequirement = (int) Math.floor(currentLimitRatio);
                }

                // 기존의 MarginRequirementHistory 찾기 또는 새로 생성
                MarginRequirementHistory history = marginRequirementHistoryRepository.findByUserId(userId)
                        .orElseGet(() -> new MarginRequirementHistory(userId, mortgageSum, creditLimit, maxLimit, marginRequirement));

                // mortgageSum, currentLimit 및 margin_requirement 값 업데이트
                history.updateValues(mortgageSum, creditLimit, maxLimit, marginRequirement);

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
                    notifyToUser(payment.getUser(), mortgageSum, marginRequirement, creditLimit, maxLimit);
                    notifyToAdmin(payment.getUser(), mortgageSum, marginRequirement, creditLimit, maxLimit, true);


                } else {
                    payment.enableRateFlag();
                    logger.info("유저 ID: {}, 현재 한도 비율이 140% 초과이므로 rateFlag를 true로 설정합니다.", userId);
                    notifyToAdmin(payment.getUser(), mortgageSum, marginRequirement, creditLimit, maxLimit, false);

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

    // 메소드에서 메시지 전송
    public void sendMessage(AssetMqDTO assetMqDTO) {
        try {
            publisher.sendMessage(assetMqDTO); // 메시지 큐로 데이터 전송
            logger.info("메시지 전송 성공: {}", assetMqDTO);
        } catch (Exception e) {
            logger.error("메시지 전송 실패: {}", e.getMessage(), e);
        }
    }

    // 사용자에게 알림 메시지 전송
    public void notifyToUser(User user, int mortgageSum, int marginRequirement, int creditLimit, int maxLimit) {
        // 알림 메시지 전송
        NotificationMessage notificationMessage = NotificationMessage.builder()
                .loginId(user.getLoginId())
                .title("담보 유지 비율 경고")
                .body(String.format("%s님, 전일 종가 기준 담보가치총액(%d)의 변동으로 인해 담보유지비율(%d)이 140보다 낮아졌습니다. 현재 한도는 %d이며, 결제 서비스가 정지되었습니다. 변동된 담보가치총액 기준 최대 한도(%d)로 줄일 경우 서비스 이용이 가능합니다.",
                        user.getLoginId(), mortgageSum, marginRequirement, creditLimit, maxLimit))
                .category(NotificationType.valueOf("담보"))
                .build();

        notificationPublisher.sendMessage(notificationMessage);
        logger.info("알림 메시지 전송 완료: {}", notificationMessage);
    }

    // 관리자에게 알림 메시지 전송
    public void notifyToAdmin(User user, int mortgageSum, int marginRequirement, int creditLimit, int maxLimit, boolean isAboveThreshold) {
        String title;
        String message;

        if (isAboveThreshold) {
            // 140% 초과일 때의 알림 메시지
            title = String.format("유저 ID: %d, 담보 유지 비율 140% 준수", user.getId());
            message = String.format("유저 %s의 담보 유지 비율이 140%%를 준수 하고있습니다. 현재 담보가치총액은 %d이고, 유지 비율은 %d%%입니다. 현재 한도는 %d이며, 최대 한도는 %d입니다.",
                    user.getLoginId(), mortgageSum, marginRequirement, creditLimit, maxLimit);
        } else {
            // 140% 이하일 때의 알림 메시지
            title = String.format("유저 ID: %d, 담보 유지 비율 140% 이하", user.getId());
            message = String.format("유저 %s의 담보 유지 비율이 140%% 이하로 떨어졌습니다. 현재 담보가치총액은 %d이고, 유지 비율은 %d%%입니다. 현재 한도는 %d이며, 최대 한도는 %d입니다. 결제 서비스가 정지되었으므로 관리가 필요합니다.",
                    user.getLoginId(), mortgageSum, marginRequirement, creditLimit, maxLimit);
        }

        MarginRequirementLogDto adminLog = MarginRequirementLogDto.builder()
                .userId(user.getId())
                .loginId(user.getLoginId())
                .mortgageSum(mortgageSum)
                .creditLimit(creditLimit)
                .marginRequirement(marginRequirement)
                .maxLimit(maxLimit)
                .title(title)
                .message(message)
                .build();

        marginRequirementLogDtoPublisher.sendMessage(adminLog);
        logger.info("관리자에게 알림 전송 완료: {}", adminLog);
    }

}
