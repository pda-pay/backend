package org.ofz.user.exception;

public class SignupPartnerApiCallException extends RuntimeException {
    public SignupPartnerApiCallException(String message) {
        super(message);
    }

    public SignupPartnerApiCallException(String message, Throwable cause) {
        super(message, cause);
    }
}