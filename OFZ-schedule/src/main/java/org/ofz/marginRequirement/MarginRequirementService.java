package org.ofz.marginRequirement;

import org.ofz.management.entity.StockInformation;
import org.ofz.management.repository.MortgagedStockRepository;
import org.ofz.management.repository.StockInformationRepository;
import org.ofz.management.utils.StockStability;
import org.ofz.payment.Payment;
import org.ofz.payment.PaymentRepository;
import org.ofz.management.entity.MortgagedStock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private RedisTemplate<String, Integer> redisTemplate;

    // Redis에서 저장된 주식 가격을 가져오는 메소드
    private Integer fetchStoredPrice(String stockCode) {
        String key = "price:" + stockCode;
        return redisTemplate.opsForValue().get(key);
    }

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 실행
    @Transactional
    public void processAllUserLimits() {
        logger.info("모든 유저의 담보 한도 비율 계산을 시작합니다.");

        List<Payment> allPayments = paymentRepository.findAll();

        for (Payment payment : allPayments) {
            Long userId = payment.getUser().getId();
            List<MortgagedStock> mortgagedStocks = mortgagedStockRepository.findByUserId(userId);

            // 담보 총액 계산
            int mortgageSum = mortgagedStocks.stream()
                    .mapToInt(stock -> stock.getQuantity() * fetchStoredPrice(stock.getStockCode()))
                    .sum();

            // 최대 한도 계산
            double maxLimit = mortgagedStocks.stream()
                    .mapToDouble(stock -> {
                        String stockCode = stock.getStockCode();
                        int stockPrice = fetchStoredPrice(stockCode);
                        StockInformation stockInformation = stockInformationRepository.findByStockCode(stockCode)
                                .orElseThrow(() -> new IllegalArgumentException("Stock information not found for stockCode: " + stockCode));
                        return StockStability.calculateLimitPrice(stockInformation.getStabilityLevel(), stockPrice) * stock.getQuantity();
                    })
                    .sum();

            // 현재 한도 비율 계산
            double currentLimitRatio = (mortgageSum / (double) payment.getCreditLimit()) * 100;

            // 최대 한도 비율 계산
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

            paymentRepository.save(payment);

            // 차후 알림 추가 시
            // 현재 한도 비율 => 낮추면 좋을 최대 한도 비율 전달
            //

        }

        logger.info("모든 유저의 담보 한도 비율 계산이 완료되었습니다.");
    }
}
