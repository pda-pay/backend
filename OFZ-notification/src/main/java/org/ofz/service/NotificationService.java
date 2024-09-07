package org.ofz.service;

import lombok.RequiredArgsConstructor;
import org.ofz.dto.CacheTokenDto;
import org.ofz.dto.SaveTokenRequest;
import org.ofz.dto.SaveUserTokenResponse;
import org.ofz.service.CacheTokenService;
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
