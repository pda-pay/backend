package org.ofz.marginRequirement.exception;

public class StockInformationNotFoundException extends RuntimeException {

    public StockInformationNotFoundException(String message) {
        super(message);
    }

    public StockInformationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}