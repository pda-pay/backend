package org.ofz.payment.exception;

import lombok.Getter;

@Getter
public class ExceededCreditLimitException extends RuntimeException {

    private String franchiseName;
    private int triedAmount;
    private String message;

    public ExceededCreditLimitException() {}

    public ExceededCreditLimitException(String franchiseName, int triedAmount, String message) {
        this.franchiseName = franchiseName;
        this.triedAmount = triedAmount;
        this.message = message;
    }
}
