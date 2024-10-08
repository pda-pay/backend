package org.ofz.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ofz.dto.redis.CacheTokenDto;
import org.ofz.dto.api.SaveTokenRequest;
import org.ofz.dto.api.TokenResponse;
import org.ofz.notificationBox.NotificationBoxRepository;
import org.ofz.rabbitMQ.NotificationType;
import org.ofz.notificationBox.entity.Notification;
import org.ofz.rabbitMQ.rabbitDto.NotificationMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationService {
    private final CacheTokenService cacheTokenService;
    private final FCMService fcmService;
    private final NotificationBoxRepository notificationBoxRepository;
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
        CacheTokenDto cacheTokenDto = cacheTokenService.getCachedToken(notificationMessage.getLoginId());

        if (isExistCacheToken(cacheTokenDto)) {
            log.info("firebase 메세지 수신: " + notificationMessage.getBody());
            fcmService.sendFcmMessageByNotificationMessage(notificationMessage, cacheTokenDto.getToken());
        }


        Notification notification = Notification.builder()
                .loginId(notificationMessage.getLoginId())
                .title(notificationMessage.getTitle())
                .content(notificationMessage.getBody())
                .notificationType(notificationMessage.getCategory())
                .build();
        notificationBoxRepository.save(notification);
    }

    private boolean isExistCacheToken(CacheTokenDto cacheTokenDto) {
        return cacheTokenDto == null ? false : true;
    }
}
