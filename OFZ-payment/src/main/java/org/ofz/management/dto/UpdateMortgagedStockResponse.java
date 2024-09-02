package org.ofz.management.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateMortgagedStockResponse {
    private final String userId;
    private final String message;

    public static UpdateMortgagedStockResponse success(String userId) {
        return new UpdateMortgagedStockResponse(userId, "MortgagedStock Change successfully.");
    }
}
