package org.ofz.user.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserErrorRes {
    private final LocalDateTime timestamp;
    private final String message;

    public UserErrorRes(LocalDateTime timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }
}
