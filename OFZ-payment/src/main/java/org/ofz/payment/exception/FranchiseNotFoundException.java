package org.ofz.payment.exception;

public class FranchiseNotFoundException extends RuntimeException {
    public FranchiseNotFoundException(String message) {
        super(message);
    }
}
