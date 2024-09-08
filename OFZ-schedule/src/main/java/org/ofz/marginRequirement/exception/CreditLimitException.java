package org.ofz.marginRequirement.exception;

public class CreditLimitException extends RuntimeException {

    public CreditLimitException(String message) {
        super(message);
    }

    public CreditLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
