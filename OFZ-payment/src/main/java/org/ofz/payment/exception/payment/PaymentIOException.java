package org.ofz.payment.exception.payment;

public class PaymentIOException extends RuntimeException {

    public PaymentIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public PaymentIOException(String message) {
        super(message);
    }
}
