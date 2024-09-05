package org.ofz.user.exception;

public class SignupDuplicationException extends RuntimeException{
    public SignupDuplicationException(String message) {
        super(message);
    }
}
