package org.ofz.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.ofz.dto.fcm.FcmMessageDto;
import org.ofz.dto.fcm.FcmSendResponse;
import org.ofz.rabbitMQ.rabbitDto.Example;
import org.ofz.rabbitMQ.rabbitDto.NotificationMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
@Service
public class FCMService {
    private final WebClient webClient;
    @Value("${firebase.config.path}")
    private String firebaseConfigPath;
    @Value("${firebase.config.url}")
    private String fcmSendUrl;

    public String getAccessToken() {
        try {
            GoogleCredentials googleCredentials = GoogleCredentials
                    .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                    .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));

            googleCredentials.refreshIfExpired();

            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            throw new RuntimeException("Failed to get access token", e);
        }
    }

    public FcmSendResponse sendFcmMessageByNotificationMessage(NotificationMessage notificationMessage, String clientToken) {
        FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
                .validateOnly(false)
                .message(FcmMessageDto.Message.builder()
                        .token(clientToken)
                        .notification(FcmMessageDto.Notification.builder()
                                .title(notificationMessage.getTitle())
                                .body(notificationMessage.getBody())
                                .build()
                        )
                        .build())
                .build();

        Mono<FcmSendResponse> fcmSendResponseMono = webClient.post()
                .uri("{fcmSendUrl}", fcmSendUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(fcmMessageDto)
                .retrieve()
                .bodyToMono(FcmSendResponse.class);
        FcmSendResponse fcmSendResponse = fcmSendResponseMono.block();

        return fcmSendResponse;
    }
}
