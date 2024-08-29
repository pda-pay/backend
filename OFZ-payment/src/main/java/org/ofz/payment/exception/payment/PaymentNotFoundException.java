package org.ofz.payment.exception.payment;

import java.util.NoSuchElementException;

public class PaymentNotFoundException extends NoSuchElementException {

    public PaymentNotFoundException(String message) {
        super(message);
    }
}
