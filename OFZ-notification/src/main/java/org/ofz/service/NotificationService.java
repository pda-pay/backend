package org.ofz.service;

import lombok.RequiredArgsConstructor;
import org.ofz.dto.redis.CacheTokenDto;
import org.ofz.dto.api.SaveTokenRequest;
import org.ofz.dto.api.TokenResponse;
import org.ofz.rabbitMQ.rabbitDto.Example;
import org.ofz.rabbitMQ.rabbitDto.NotificationMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class NotificationService {
    private final CacheTokenService cacheTokenService;
    private final FCMService fcmService;
    private final static String QUEUE_NAME = "notification";

    public TokenResponse saveUserToken(String userId, SaveTokenRequest saveTokenRequest) {
        cacheTokenService.cacheToken(new CacheTokenDto(userId, saveTokenRequest.getToken()));

        return TokenResponse.saveSuccess();
    }

    public TokenResponse deleteUserToken(String userId) {
        cacheTokenService.deleteCachedToken(userId);

        return TokenResponse.deleteSuccess();
    }

    public void test() throws IOException {
        System.out.println(fcmService.getAccessToken());
    }

    @RabbitListener(queues = QUEUE_NAME)
    public void receiveMessage(NotificationMessage notificationMessage) {
        CacheTokenDto cacheTokenDto = cacheTokenService.getCachedToken(notificationMessage.getUserId());
        fcmService.sendFcmMessageByNotificationMessage(notificationMessage, cacheTokenDto.getToken());
    }
}
