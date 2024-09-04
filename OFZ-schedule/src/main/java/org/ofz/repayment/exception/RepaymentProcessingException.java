package org.ofz.repayment.exception;

public class RepaymentProcessingException extends RuntimeException {
    public RepaymentProcessingException(String message) {
        super(message);
    }

    public RepaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
