package org.ofz.cacheStockPrice.service;

import lombok.RequiredArgsConstructor;
import org.ofz.cacheStockPrice.dto.PreviousStockPriceResponse;
import org.ofz.management.repository.StockInformationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StockPriceScheduleService {
    private final StockInformationRepository stockInformationRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final WebClient webClient;

    @Value("${webclient.base-url}")
    private String baseUrl;
    @Scheduled(cron = "0 0 0 * * *")
    public void cachePreviousStockPrice() {
        List<String> stockCodes = stockInformationRepository.findAllStockCodes();

        for (String stockCode : stockCodes) {
            String key = "price:" + stockCode;
            Integer value = fetchPreviousStockPrice(stockCode);
            redisTemplate.opsForValue().set(key, value, Duration.ofHours(24));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }

    private int  fetchPreviousStockPrice(String stockCode) {
        Mono<PreviousStockPriceResponse> previousStockPriceResponseMono = webClient.get()
                .uri("{baseUrl}/securities/stocks/{stockCode}", baseUrl, stockCode)
                .retrieve()
                .bodyToMono(PreviousStockPriceResponse.class);
        PreviousStockPriceResponse previousStockPriceResponse = previousStockPriceResponseMono.block();

        return previousStockPriceResponse.getAmount();
    }
}
