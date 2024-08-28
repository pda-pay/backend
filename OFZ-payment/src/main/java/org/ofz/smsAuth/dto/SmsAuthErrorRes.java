package org.ofz.smsAuth.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SmsAuthErrorRes {
    private final LocalDateTime timestamp;
    private final String message;
    private final String errorCode;

    public SmsAuthErrorRes(LocalDateTime timestamp, String message, String errorCode) {
        this.timestamp = timestamp;
        this.message = message;
        this.errorCode = errorCode;
    }
}
