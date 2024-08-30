package org.ofz.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UserSignupStockDataRes {
    private List<Account> accounts;

    @Getter
    @NoArgsConstructor
    public static class Account {
        private String accountNumber;
        private int deposit;
        private String companyCode;
        private String category;
        private List<Stock> stocks;
    }

    @Getter
    @NoArgsConstructor
    public static class Stock {
        private int quantity;
        private String stockCode;
    }
}
