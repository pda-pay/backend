package org.ofz.smsAuth.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SmsAuthErrorRes {
    private final LocalDateTime timestamp;
    private final String message;

    public SmsAuthErrorRes(LocalDateTime timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }
}
