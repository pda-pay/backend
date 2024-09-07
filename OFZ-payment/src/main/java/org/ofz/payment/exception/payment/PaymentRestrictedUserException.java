package org.ofz.payment.exception.payment;

public class PaymentRestrictedUserException extends RuntimeException {
    public PaymentRestrictedUserException(String message) {
        super(message);
    }
}
