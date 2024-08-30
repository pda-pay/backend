package org.ofz.user.exception;

public class SignupStockDataSaveException extends RuntimeException {
    public SignupStockDataSaveException(String message) {
        super(message);
    }

    public SignupStockDataSaveException(String message, Throwable cause) {
        super(message, cause);
    }

}
