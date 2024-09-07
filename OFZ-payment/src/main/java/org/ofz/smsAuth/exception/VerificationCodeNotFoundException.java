package org.ofz.smsAuth.exception;

public class VerificationCodeNotFoundException extends RuntimeException {
    public VerificationCodeNotFoundException(String message) {
        super(message);
    }
}
