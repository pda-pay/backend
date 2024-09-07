package org.ofz;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationService {
    private final CacheTokenService cacheTokenService;
    public SaveUserTokenResponse saveUserToken(String userId, SaveTokenRequest saveTokenRequest) {
        cacheTokenService.cacheToken(new CacheTokenDto(userId, saveTokenRequest.getToken()));

        return SaveUserTokenResponse.sucess();
    }
}
