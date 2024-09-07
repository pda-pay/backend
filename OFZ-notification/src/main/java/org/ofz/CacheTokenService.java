package org.ofz;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CacheTokenService {
    private final RedisTemplate<String, Object> redisTemplate;
    private String configureNoticeTokenKey(String userLoginId) {
        return prifixKey + userLoginId;
    }
    private static final String prifixKey = "notification:";
    public void cacheToken(CacheTokenDto cacheTokenDto) {
        redisTemplate.opsForValue().set(configureNoticeTokenKey(cacheTokenDto.getUserLoginId()), cacheTokenDto, Duration.ofDays(30));
    }

    public CacheTokenDto getCachedToken(String userLoginId) {
        return (CacheTokenDto) redisTemplate.opsForValue().get(configureNoticeTokenKey(userLoginId));
    }
}
