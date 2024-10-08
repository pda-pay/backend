package org.ofz.management.service;

import lombok.RequiredArgsConstructor;
import org.ofz.management.dto.common.AccountDto;
import org.ofz.management.dto.common.MortgagedStockDto;
import org.ofz.management.dto.common.StockPriorityDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagementCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String prefixKey = "payment:";
    private String configureMortgagedStockKey(String userLoginId) {
        return prefixKey + String.format("%s:", userLoginId) + "mortgagedStock";
    }

    private String configureStockPriorityKey(String userLoginId) {
        return prefixKey + String.format("%s:", userLoginId) + "stockPriority";
    }

    private String configureLimitKey(String userLoginId) {
        return prefixKey + String.format("%s:", userLoginId) + "limit";
    }

    private String configureAccountKey(String userLoginId) {
        return prefixKey + String.format("%s:", userLoginId) + "account";
    }

    private String configureDateKey(String userLoginId) {
        return prefixKey + String.format("%s:", userLoginId) + "date";
    }

    public void cacheMortgagedStocks(String userLoginId, List<MortgagedStockDto> mortgagedStockDtos) {
        redisTemplate.opsForValue().set(configureMortgagedStockKey(userLoginId), mortgagedStockDtos, Duration.ofMinutes(30));
    }

    public List<MortgagedStockDto> getCachedMortgagedStocks(String userLoginId) {
        return (List<MortgagedStockDto>) redisTemplate.opsForValue().get(configureMortgagedStockKey(userLoginId));
    }

    public void deleteCachedMortgagedStocks(String userLoginId) {
        redisTemplate.delete(configureMortgagedStockKey(userLoginId));
    }

    public void cacheStockPriorities(String userLoginId, List<StockPriorityDto> stockPriorityDtos) {
        redisTemplate.opsForValue().set(configureStockPriorityKey(userLoginId), stockPriorityDtos, Duration.ofMinutes(30));
    }

    public List<StockPriorityDto> getCachedStockPriorities(String userLoginId) {
        return (List<StockPriorityDto>) redisTemplate.opsForValue().get(configureStockPriorityKey(userLoginId));
    }

    public void deleteCachedStockPriorities(String userLoginId) {
        redisTemplate.delete(configureStockPriorityKey(userLoginId));
    }

    public void cacheLimit(String userLoginId, Integer limit) {
        redisTemplate.opsForValue().set(configureLimitKey(userLoginId), limit, Duration.ofMinutes(30));
    }

    public Integer getCachedLimit(String userLoginId) {
        return (Integer) redisTemplate.opsForValue().get(configureLimitKey(userLoginId));
    }

    public void deleteCachedLimit(String userLoginId) {
        redisTemplate.delete(configureLimitKey(userLoginId));
    }

    public void cacheAccount(String userLoginId, AccountDto accountDto) {
        redisTemplate.opsForValue().set(configureAccountKey(userLoginId), accountDto, Duration.ofMinutes(30));
    }

    public AccountDto getCachedAccount(String userLoginId) {
        return (AccountDto) redisTemplate.opsForValue().get(configureAccountKey(userLoginId));
    }

    public void cacheDate(String userLoginId, Integer date) {
        redisTemplate.opsForValue().set(configureDateKey(userLoginId), date, Duration.ofMinutes(30));
    }

    public Integer getCachedDate(String userLoginId) {
        return (Integer) redisTemplate.opsForValue().get(configureDateKey(userLoginId));
    }
}
