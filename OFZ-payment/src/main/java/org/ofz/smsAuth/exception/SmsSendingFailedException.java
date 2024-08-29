package org.ofz.smsAuth.exception;

public class SmsSendingFailedException extends RuntimeException {
    public SmsSendingFailedException(String message) {
        super(message);
    }
}