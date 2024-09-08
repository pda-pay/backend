package org.ofz.controller;

import lombok.RequiredArgsConstructor;
import org.ofz.service.NotificationService;
import org.ofz.dto.api.SaveTokenRequest;
import org.ofz.dto.api.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;
    @PostMapping("/tokens")
    public ResponseEntity<TokenResponse> saveUserFCMToken(@RequestHeader("X-LOGIN-ID") String userId, @RequestBody SaveTokenRequest saveTokenRequest) {
        TokenResponse tokenResponse = notificationService.saveUserToken(userId, saveTokenRequest);

        return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
    }

    @PutMapping("/tokens")
    public ResponseEntity<TokenResponse> deleteUserFCMToken(@RequestHeader("X-LOGIN-ID") String userId) {
        TokenResponse tokenResponse = notificationService.deleteUserToken(userId);

        return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
    }

    @GetMapping("/tokens/test")
    public ResponseEntity<?> test() throws IOException {
        notificationService.test();

        return ResponseEntity.status(HttpStatus.OK).body("good");
    }
}