package org.ofz.dto.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private String message;
    public static TokenResponse saveSuccess() {
        return new TokenResponse("notification token Save success.");
    }

    public static TokenResponse deleteSuccess() {
        return new TokenResponse("notification token Delete success.");
    }
}
