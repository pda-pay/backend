package org.ofz.payment.exception.payment;

import io.jsonwebtoken.JwtException;

public class NonValidPaymentTokenException extends JwtException {
    public NonValidPaymentTokenException(String message) {
        super(message);
    }
}
