package org.ofz.management.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class UserStockResponses {
    private final List<UserStockResponse> userStocks;
    private final int totalDebt;
}
