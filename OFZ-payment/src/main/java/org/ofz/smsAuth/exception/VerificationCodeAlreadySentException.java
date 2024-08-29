package org.ofz.smsAuth.exception;

public class VerificationCodeAlreadySentException extends RuntimeException {
    public VerificationCodeAlreadySentException(String message) {
        super(message);
    }
}