package org.ofz.repayment.exception.repayment;

public class TooHighPawnStockQuantityException extends RuntimeException {

    public TooHighPawnStockQuantityException(String message) {
        super(message);
    }
}
