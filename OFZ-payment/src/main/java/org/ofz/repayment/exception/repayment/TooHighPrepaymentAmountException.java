package org.ofz.repayment.exception.repayment;

public class TooHighPrepaymentAmountException extends RuntimeException {

    public TooHighPrepaymentAmountException(String message) {
        super(message);
    }
}
