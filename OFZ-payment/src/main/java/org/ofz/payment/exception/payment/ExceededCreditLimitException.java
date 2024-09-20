package org.ofz.payment.exception.payment;

import lombok.Getter;
import org.ofz.payment.entity.Franchise;
import org.ofz.user.User;

@Getter
public class ExceededCreditLimitException extends RuntimeException {

    private User user;
    private Franchise franchise;
    private int triedAmount;
    private int creditLimit;
    private String message;

    public ExceededCreditLimitException() {}

    public ExceededCreditLimitException(User user, Franchise franchise, int triedAmount, int creditLimit, String message) {
        this.user = user;
        this.franchise = franchise;
        this.triedAmount = triedAmount;
        this.creditLimit = creditLimit;
        this.message = message;
    }
}
