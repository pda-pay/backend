package org.ofz.asset.exception;

public class GenericServiceException extends RuntimeException {
    public GenericServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}