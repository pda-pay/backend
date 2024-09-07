package org.ofz.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.service.NotificationService;
import org.ofz.dto.SaveTokenRequest;
import org.ofz.dto.SaveUserTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;
    @PostMapping("/tokens")
    public ResponseEntity<SaveUserTokenResponse> saveUserFCMToken(@RequestHeader("X-LOGIN-ID") String userId, @RequestBody SaveTokenRequest saveTokenRequest) {
        SaveUserTokenResponse saveUserTokenResponse = notificationService.saveUserToken(userId, saveTokenRequest);

        return ResponseEntity.status(HttpStatus.OK).body(saveUserTokenResponse);
    }
}