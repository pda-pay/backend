package org.ofz.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    public void addBlackList(String token, Long expiration){
        redisTemplate.opsForValue().set(token, "logout", Duration.ofMillis(expiration));
    }

    public boolean hasKeyBlackList(String token){
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }
}
