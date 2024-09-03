package org.ofz.management.dto.api.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SavedResponse {
    private final String userId;
    private final String message;

    public static SavedResponse success(String userId) {
        return new SavedResponse(userId, "saved success.");
    }
}
