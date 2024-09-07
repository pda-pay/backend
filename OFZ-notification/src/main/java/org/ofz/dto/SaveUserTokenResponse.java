package org.ofz.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SaveUserTokenResponse {
    private String message;
    public static SaveUserTokenResponse sucess() {
        return new SaveUserTokenResponse("notification token save success.");
    }
}
