package org.ofz.management.service;

import lombok.RequiredArgsConstructor;
import org.ofz.management.dto.MortgagedStockRequest;
import org.ofz.management.dto.StockPriorityRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagementCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String prifixKey = "payment:";
    private String configureMortgagedStockKey(String userLoginId) {
        return prifixKey + String.format("%s:",userLoginId) + "mortgagedStock";
    }

    private String configureStockPriorityKey(String userLoginId) {
        return prifixKey + String.format("%s::",userLoginId) + "stockPriority";
    }

    private String configureLimitKey(String userLoginId) {
        return prifixKey + String.format("%s:",userLoginId) + "limit";
    }

    private String configureAccountKey(String userLoginId) {
        return prifixKey + String.format("%s:",userLoginId) + "account";
    }

    private String configureDateKey(String userLoginId) {
        return prifixKey + String.format("%s:",userLoginId) + "date";
    }

    private String configurePasswordKey(String userLoginId) {
        return prifixKey + String.format("%s:",userLoginId) + "password";
    }

    public void cacheMortgagedStockRequests(String userLoginId, List<MortgagedStockRequest> mortgagedStockRequests) {
        redisTemplate.opsForValue().set(configureMortgagedStockKey(userLoginId), mortgagedStockRequests, Duration.ofMinutes(10));
    }

    public List<MortgagedStockRequest> getCachedMortgagedStockRequests(String userLoginId) {
        return (List<MortgagedStockRequest>) redisTemplate.opsForValue().get(configureMortgagedStockKey(userLoginId));
    }

    public void cacheStockPriorityRequests(String userLoginId, List<StockPriorityRequest> stockPriorityRequests) {
        redisTemplate.opsForValue().set(configureStockPriorityKey(userLoginId), stockPriorityRequests, Duration.ofMinutes(10));
    }

    public List<StockPriorityRequest> getCachedStockPriorityRequests(String userLoginId) {
        return (List<StockPriorityRequest>) redisTemplate.opsForValue().get(configureStockPriorityKey(userLoginId));
    }

    public void cacheLimit(String userLoginId, Integer limit) {
        redisTemplate.opsForValue().set(configureLimitKey(userLoginId), limit, Duration.ofMinutes(10));
    }

    public Integer getCachedLimit(String userLoginId) {
        return (Integer) redisTemplate.opsForValue().get(configureLimitKey(userLoginId));
    }

    public void cacheAccount(String userLoginId, String account) {
        redisTemplate.opsForValue().set(configureAccountKey(userLoginId), account, Duration.ofMinutes(10));
    }

    public String getCachedAccount(String userLoginId) {
        return (String) redisTemplate.opsForValue().get(configureAccountKey(userLoginId));
    }

    public void cacheDate(String userLoginId, Integer date) {
        redisTemplate.opsForValue().set(configureDateKey(userLoginId), date, Duration.ofMinutes(10));
    }

    public Integer getCachedDate(String userLoginId) {
        return (Integer) redisTemplate.opsForValue().get(configureDateKey(userLoginId));
    }

    public void cachePassword(String userLoginId, String password) {
        redisTemplate.opsForValue().set(configurePasswordKey(userLoginId), password, Duration.ofMinutes(10));
    }

    public String getCachedPassword(String userLoginId) {
        return (String) redisTemplate.opsForValue().get(configurePasswordKey(userLoginId));
    }
}
