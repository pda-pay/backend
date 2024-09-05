package org.ofz.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String LOGOUT_PREFIX = "logoutList:";
    private static final String PREVIOUS_PRICE_PREFIX = "price:";

    public void addBlackList(String token, Long expiration){
        redisTemplate.opsForValue().set(LOGOUT_PREFIX + token, "logout", Duration.ofMillis(expiration));
    }

    public boolean hasKeyBlackList(String token){
        return Boolean.TRUE.equals(redisTemplate.hasKey(LOGOUT_PREFIX + token));
    }

    public void addPaymentToken(String token, long expirationTimeInMillis) {
        redisTemplate.opsForValue().set(token, "alive", expirationTimeInMillis, TimeUnit.MILLISECONDS);
    }

    public boolean validatePaymentToken(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }

    public void deletePaymentToken(String token) {
        redisTemplate.delete(token);
    }

    public Integer fetchStoredPreviousPrice(String stockCode) {
        String key = PREVIOUS_PRICE_PREFIX + stockCode;
        return (Integer) redisTemplate.opsForValue().get(key);
    }
}
