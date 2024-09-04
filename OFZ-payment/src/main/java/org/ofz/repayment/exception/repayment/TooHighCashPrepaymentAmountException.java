package org.ofz.repayment.exception.repayment;

public class TooHighCashPrepaymentAmountException extends RuntimeException {

    public TooHighCashPrepaymentAmountException(String message) {
        super(message);
    }
}
