package org.ofz.payment.exception.payment;

import io.jsonwebtoken.ExpiredJwtException;

public class PaymentTokenExpiredException extends ExpiredJwtException {

    public PaymentTokenExpiredException(String message) {
        super(null, null, message);
    }
}
